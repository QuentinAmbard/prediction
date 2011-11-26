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
    if candidat['candidatName'] == "SARKOZY":
        sarkozy_query = build_query(candidat)
    if candidat['candidatName'] == "HOLLANDE":
        hollande_query = build_query(candidat)        
        
j=0
for candidat in db.candidat.find({}):
    if j>1:
        #break
        time.sleep(15)
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
            if len(row) ==0 or row[0] == "" or row[0] == " ":
                in_main = False
                if in_region:
                    break
            if in_main:
                print ', '.join(row)
                date=time.strptime(row[0],"%Y-%m-%d")
                timestamp = long(time.mktime(date)*1000)
                if len(row) <4:
                    value = "0"
                else :
                    value=row[3]
                reports = db.report.find({"timestamp": timestamp})
                print(value)
                if value != "" and value != " ":
                    print(timestamp)
                    if reports.count() >0:
                        report = reports[0]
                        if candidat['candidatName'] not in report['candidats']:
                            report['candidats'][candidat['candidatName']] = {}
                        report['candidats'][candidat['candidatName']]['insight'] = int(value)
                        report['timestamp'] = timestamp
                        db.report.update({"_id": report['_id']}, report)
                    else :
                        report = {}
                        report['candidats'] = {candidat['candidatName']: {}}
                        report['timestamp'] = timestamp
                        report['candidats'][candidat['candidatName']]['insight'] = int(value)
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


