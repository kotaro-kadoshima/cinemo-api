export SPRING_DATASOURCE_URL="jdbc:postgresql://ep-mute-truth-adb5eovo-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require&channelBinding=require"
export SPRING_DATASOURCE_USERNAME="neondb_owner"
export SPRING_DATASOURCE_PASSWORD="npg_zD6ypOk8UXFm"
export SPRING_DATASOURCE_HIKARI_JDBC_URL="$SPRING_DATASOURCE_URL"


# IDEを使用せずにターミナルから直接実行する場合はまずこのファイルを実行すること
# source local.sh
# mvn spring-boot:run or mvn test