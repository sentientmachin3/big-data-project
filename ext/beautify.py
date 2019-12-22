UNKNOWNS = 5
try:
    deltas = open('deltas.txt')
except IOError:
    print("IOERROR: can't open deltas.txt")
    
rankings = {}
for i in range(0, UNKNOWNS):
    rankings['unknown' + str(i+1)] = []

for line in deltas:
    unk = line.split('-')[1]
    kn = line.split('-')[0]
    if (kn not in rankings[unk]):
        rankings[unk].append(kn)

for rank in range(0, len(rankings)):
    print('unknown' + str(rank + 1) + ' = ' + rankings['unknown' + str(rank + 1)][0])
    rank += 1
