UNKNOWNS = 5
deltas = open('deltas.txt')
rankings = {}
for i in range(0, UNKNOWNS):
    rankings['unknown' + str(i+1)] = []

for line in deltas:
    unk = line.split('-')[1]
    kn = line.split('-')[0]
    if (kn not in rankings[unk]):
        rankings[unk].append(kn)

rank = 1
for key in rankings:
    print(str(rank) + '. ' + key + ' = ' + rankings[key][0])
    rank += 1
# print(rankings)

