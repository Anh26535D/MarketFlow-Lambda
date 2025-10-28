# data_integration_numeric.py
import os
import pandas as pd
from rapidfuzz import fuzz
import numpy as np

# --- Config ---
input_dir = "./input"
output_dir = "./output"
output_file = os.path.join(output_dir, "merged_dataset.csv")
max_sample = 100  # for data similarity checks
name_threshold = 50  # lower for better fuzzy matching
value_threshold = 0.5
numeric_tol = 0.01  # tolerance for numeric comparison

os.makedirs(output_dir, exist_ok=True)

# --- Step 1: Load CSVs ---
csv_files = [f for f in os.listdir(input_dir) if f.endswith(".csv")]
dataframes = []
for f in csv_files:
    df = pd.read_csv(os.path.join(input_dir, f))
    df.columns = df.columns.str.strip().str.lower()
    dataframes.append(df)

print(f"Loaded {len(dataframes)} CSV files.")

# --- Step 2: Column Mapping ---
mapped_columns = {}

for col in dataframes[0].columns:
    mapped_columns[col] = [(0, col)]

def adaptive_sample(series, max_sample=max_sample):
    series = series.dropna().astype(str)
    n = min(len(series), max_sample)
    if n == 0:
        return pd.Series([], dtype=str)
    return series.sample(n, random_state=1)

def numeric_overlap(series_a, series_b, tol=numeric_tol):
    """Return fraction of values that match numerically within tolerance."""
    a = pd.to_numeric(series_a, errors='coerce').dropna()
    b = pd.to_numeric(series_b, errors='coerce').dropna()
    if len(a) == 0 or len(b) == 0:
        return 0.0
    return np.mean([np.any(np.isclose(val, b, atol=tol)) for val in a])

for i, df in enumerate(dataframes[1:], start=1):
    for col in df.columns:
        candidate_cols = list(mapped_columns.keys())
        best_match = None
        best_score = 0

        # 1️⃣ Fuzzy name matching
        for key in candidate_cols:
            name_score = fuzz.token_sort_ratio(col.lower(), key.lower())
            if name_score > best_score:
                best_score = name_score
                best_match = key

        if best_score >= name_threshold:
            mapped_columns[best_match].append((i, col))
            continue

        # 2️⃣ Numeric/string value overlap
        matched = False
        for key in candidate_cols:
            overlaps = []
            for df_index, orig_col in mapped_columns[key]:
                series_a = adaptive_sample(dataframes[df_index][orig_col])
                series_b = adaptive_sample(df[col])
                num_overlap = numeric_overlap(series_a, series_b)
                str_overlap = len(set(series_a.astype(str)) & set(series_b.astype(str))) / max(1, min(len(series_a), len(series_b)))
                overlaps.append(max(num_overlap, str_overlap))
            if max(overlaps) >= value_threshold:
                mapped_columns[key].append((i, col))
                matched = True
                break

        # 3️⃣ Create new column if no match
        if not matched:
            mapped_columns[col] = [(i, col)]

# --- Step 2b: Print column pair similarities ---
print("\nColumn similarity summary:")
for i, df_a in enumerate(dataframes):
    for j, df_b in enumerate(dataframes):
        if i >= j:
            continue
        for col_a in df_a.columns:
            for col_b in df_b.columns:
                name_score = fuzz.token_sort_ratio(col_a.lower(), col_b.lower())
                series_a = adaptive_sample(df_a[col_a])
                series_b = adaptive_sample(df_b[col_b])
                value_overlap = max(numeric_overlap(series_a, series_b),
                                    len(set(series_a.astype(str)) & set(series_b.astype(str))) / max(1, min(len(series_a), len(series_b))))
                if name_score >= 50 or value_overlap >= 0.1:
                    print(f"{col_a} (df{i}) vs {col_b} (df{j}) -> Name: {name_score}, Value overlap: {value_overlap:.2f}")

# --- Step 3: Merge row-wise (stack columns) ---
final_df = pd.DataFrame()

for final_col, mappings in mapped_columns.items():
    combined = pd.Series(dtype=object)
    for df_index, orig_col in mappings:
        combined = pd.concat([combined, dataframes[df_index][orig_col]], ignore_index=True)
    final_df[final_col] = combined

# --- Step 4: Save final merged dataset ---
final_df.to_csv(output_file, index=False)
print(f"Merged dataset saved to {output_file}")

# --- Step 5: Column mapping summary ---
print("\nColumn mapping summary:")
for final_col, mappings in mapped_columns.items():
    print(f"{final_col}: {mappings}")
