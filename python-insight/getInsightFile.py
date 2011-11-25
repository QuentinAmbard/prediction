# coding=UTF-8  baby !
"""
sudo apt-get install python-pip
sudo apt-get install python-dev
sudo pip install pymongo
"""

from pymongo import Connection
from pyGTrends import pyGTrends
import csv
import time

regions = {"Provence-Alpes-Côte d'Azur":"PACA", 
           "Rhône-Alpes":"RHONE_ALPES", 
           "Alsace":"ALSACE", 
           "Aquitaine":"AQUITAINE", 
           "Auvergne":"AUVERGNE", 
           "Bourgogne":"BOURGOGNE", 
           "Bretagne":"BRETAGNE", 
           "Centre":"CENTRE", 
           "Champagne-Ardenne":"CHAMPAGNE_ARDENNE", 
           "Corse":"CORSE", 
           "Franche-Comté":"FRANCHE_COMPTE", 
           "Île-de-France":"ILE_DE_FRANCE", 
           "Languedoc-Roussillon":"LANGUEDOC_ROUSSILLON", 
           "Limousin":"LIMOUSIN", 
           "Lorraine":"LORRAINE", 
           "Midi-Pyrénées":"MIDI_PYRENEES",
           "Nord-Pas-de-Calais":"NORD_PAS_DE_CALAIS", 
           "Basse-Normandie":"BASSE_NORMANDIE", 
           "Haute-Normandie":"HAUTE_NORMANDIE", 
           "Pays de la Loire":"PAYS_DE_LA_LOIRE", 
           "Picardie":"PICARDIE",
           "Poitou-Charentes":"POITOU_CHARENTES"}


def build_query(candidat):
    query = ""
    for nickname in candidat['nicknames']:
        query +="+"+nickname
    return query[1:]

connector = pyGTrends('avricot.team','table87table')

connection = Connection('localhost', 27017)
db = connection.prediction
for candidat in db.candidat.find({}):
    if candidat['name'] == "SARKOZY":
        sarkozy_query = build_query(candidat)
    if candidat['name'] == "HOLLANDE":
        hollande_query = build_query(candidat)        
        
j=0
for candidat in db.candidat.find({}):
    if j>1:
        break
    j=j+1
    queries = [sarkozy_query, hollande_query]
    queries.append(build_query(candidat))
    print(queries)
    raw_data = connector.download_report(queries)
    lines = raw_data.split('\n')
    spamReader = csv.reader(lines, delimiter=',')
    in_main = True
    in_region = False
    i=0
    for row in spamReader:
        #skip the 3 first lines
        if i>5:
            print ', '.join(row)
            if len(row) ==0 or row[0] == "" or row[0] == " ":
                in_main = False
                if in_region:
                    break
            if in_main:
                date=time.strptime(row[0],"%Y-%m-%d")
                timestamp = long(time.mktime(date)*1000)
                value=row[3]
                reports = db.candidat.find({"timestamp": timestamp, "candidat": candidat['name']})
                print(value)
                print(timestamp)
                if reports.count() >0:
                    report = reports[0]
                    report['insight'] = value
                    db.report.update({"_id": report['_id']}, report)
                else :
                    report = {}
                    report['candidat'] = candidat['name']
                    report['insight'] = value
                    report['timestamp'] = timestamp
                    db.report.save(report)
            if in_region:
                region = regions[row[0]]
                value = int(row[1])
                print(region)
                candidat['geoReport'][region] = value
            elif len(row)>0 and row[0] == "Sous-région":
                in_region = True
        i=i+1
    db.candidat.update({"_id": candidat['_id']}, candidat)
#print connector.csv()


