# Run the app

START_SCRIPT="$KAFKA_STREAMS_ROOT_DIR"/app/build/install/app/bin/app

if [[ ! -e "$START_SCRIPT" ]]; then
  echo "$START_SCRIPT does not exist. Build the project first before running." >&2
  exit 1;
fi

"$START_SCRIPT"