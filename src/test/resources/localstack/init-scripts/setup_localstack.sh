echo "Starting localstack initialization..."

awslocal s3api create-bucket --bucket stock-taking --region us-east-2

echo "Initialization has finished!"