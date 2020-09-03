# Using SES and Quarkus Lambda to send emails received via SES.

# AWS Setup

- Create a Service role **lambda-s3-email-role** - to allow the Lambda to execute
  - added AWSLambdaExecute Policy to allow access to S3
  - added **lambda-send**  
    - custom policy to allow sending emails
  - added **s3 read only policy**
- S3 Bucket
  - Configured SES to deliver mails received to an S3 Bucket
- Add S3 trigger to the deployed Lambda


# Building/Testing

- Build/Package
~~~
./mvnw clean package
~~~
- Using SAM Local to test the Lambda.
~~~
sam local invoke --template target/sam.jvm.yaml --event payload.json
~~~
- Using the Quarkus S3 client extentions.
https://quarkus.io/guides/amazon-s3

Build the Native Image
./mvnw clean package -Pnative -Dnative-image.docker-build=true

Set the Role ARN
export LAMBDA_ROLE_ARN="arn:aws:iam::123456:role/lambda-s3-email-role"

Use the script to deploy
sh target/manage.sh native create
