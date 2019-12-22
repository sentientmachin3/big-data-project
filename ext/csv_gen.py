import csv

allowed_fields = ['articles', 'prepositions','conjunctions','avg_period_length','commas', 'dialogues']

# authors list
authors = []
for line in open('known-frequencies.txt'):
    line.rstrip('\n')
    if line.split('-')[0] not in authors:
         authors.append(line.split('-')[0])

# map init
frequencies = {}
for a in authors:
    frequencies[a] = {}

for line in open('known-frequencies.txt'):
    line.rstrip('\n')
    csv_append_str = ''
    value = line.split('=')[1]
    author=line.split('-')[0]
    field = (line.split('=')[0].split('-')[2]).rstrip('\n')
    if field in allowed_fields:
        frequencies[author][field] = float(value)

# writing to csv
with open('known-frequencies.csv', mode='w') as frequencies_file:
    freq_writer = csv.writer(frequencies_file, delimiter=',')
    # freq_writer.writerow(['John Smith', 'Accounting', 'November'])
    for key in frequencies:
            freq_writer.writerow([key,frequencies[key]['articles'],frequencies[key]['conjunctions'],frequencies[key]['prepositions'],frequencies[key]['avg_period_length'],frequencies[key]['dialogues'],frequencies[key]['commas']])