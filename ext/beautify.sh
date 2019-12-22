rm -f deltas.txt
docker cp cloudera:/home/deltas.txt ./
python beautify.py
