# Run the app

START_SCRIPT="$KAFKA_STREAMS_ZIP_CODES_ROOT_DIR"/build/install/streams-zip-codes/bin/streams-zip-codes

if [[ ! -e "$START_SCRIPT" ]]; then
  echo "$START_SCRIPT does not exist. Build the project first before running." >&2
  exit 1;
fi

"$START_SCRIPT"
