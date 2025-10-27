# Base image
FROM apache/spark:3.5.1

# Create writable Ivy cache folder (fixes the missing cache issue)
USER root
RUN mkdir -p /home/spark/.ivy2/cache && \
    chown -R root:root /home/spark/.ivy2

# Optional: add your spark jobs inside container
# COPY ./spark-jobs /opt/spark-jobs

# Return to default Spark user if required
USER root

WORKDIR /opt/spark