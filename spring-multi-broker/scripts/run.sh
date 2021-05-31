# Run the app

set -eu

JAR=$(ls "$SPRING_MULTI_BROKER_ROOT_DIR"/build/libs/*.jar)

if [[ ! -e "$JAR" ]]; then
  echo "$JAR does not exist. Build the project first before running." >&2
  exit 1;
fi

java -jar "$JAR"
