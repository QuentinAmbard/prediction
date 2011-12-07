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

regions = {
           "Alsace": {"name": "ALS", "id": "FR-A"} ,
           "Aquitaine": {"name": "AQU", "id": "FR-B"} ,
           "Auvergne": {"name": "AUV", "id": "FR-C"} ,
           "Basse-Normandie": {"name": "BNO", "id": "FR-P"} ,
           "Bourgogne": {"name": "BOU", "id": "FR-D"} ,
           "Bretagne": {"name": "BRE", "id": "FR-E"} ,
           "Centre": {"name": "CEN" ,"id": "FR-F"} ,
           "Champagne-Ardenne": {"name": "CHA", "id": "FR-G"} ,
           "Corse": {"name": "COR", "id": "FR-H"} ,
           "Franche-Comté": {"name": "FRA", "id": "FR-I"} ,
           "Haute-Normandie": {"name": "HNO", "id": "FR-Q"} ,
           "Île-de-France": {"name": "ILE", "id": "FR-J"} ,
           "Languedoc-Roussillon": {"name": "LAN", "id": "FR-K"} ,
           "Limousin": {"name": "LIM", "id": "FR-L"} ,
           "Lorraine": {"name": "LOR", "id": "FR-M"} ,
           "Midi-Pyrénées": {"name": "MID", "id": "FR-N"} ,
           "Nord-Pas-de-Calais": {"name": "NOR", "id": "FR-O"} ,
           "Pays de la Loire": {"name": "PAY", "id": "FR-R"} ,
           "Picardie": {"name": "PIC", "id": "FR-S"},
           "Poitou-Charentes": {"name": "POI", "id": "FR-T"},
           "Provence-Alpes-Côte d'Azur": {"name": "PAC", "id": "FR-U"}, 
           "Rhône-Alpes":{"name": "RHO", "id": "FR-V"} }


def build_query(candidat):
    query = ""
    for nickname in candidat['nicknames']:
        query +="+"+nickname
    return query[1:]

def update_report(candidat, value, timestamp, regionName):
    if value != "" and value != " ":
        if regionName=="":
            reports = db.report.find({"timestamp": timestamp})
            print(value)
            print(timestamp)
            if reports.count() >0:
                report = reports[0]
                if candidat['candidatName'] not in report['candidats']:
                    report['candidats'][candidat['candidatName']] = {}
                if regionName=="":
                    report['candidats'][candidat['candidatName']]['insight'] = int(value)
                db.report.update({"_id": report['_id']}, report)
            else :
                report = {}
                report['candidats'] = {candidat['candidatName']: {'candidatName' : candidat['candidatName']}}
                report['timestamp'] = timestamp
                report['candidats'][candidat['candidatName']]['insight'] = int(value)
                db.report.save(report)
        else:
            reports = db.georeport.find({"timestamp": timestamp, "candidatName": candidat['candidatName']})
            if reports.count() >0:
                report = reports[0]
                report['report'][regionName] =  int(value)
                db.georeport.update({"_id": report['_id']}, report)
            else:
                report = {}
                report['candidatName'] = candidat['candidatName']
                report['timestamp'] = timestamp
                report['report'] = {regionName: int(value)}
                db.georeport.save(report)
            
def get_timestamp (date_str):
    date=time.strptime(date_str,"%Y-%m-%d")
    timestamp = long(time.mktime(date)*1000)
    return timestamp

def handle_data (raw_data, candidat, regionName=""):
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
                #Stop if needed
                if in_region or regionName!="":
                    break
            if in_main:
                print ', '.join(row)
                if len(row) <4:
                    value = "0"
                else :
                    value=row[3]
                    
                column_date = row[0]
                index_of_separator = column_date.find(" - ")
                if index_of_separator  == -1:
                    timestamp = get_timestamp(column_date)
                    update_report(candidat, value, timestamp, regionName)
                else:
                    timestamp = get_timestamp(column_date[:index_of_separator])
                    timestamp2 = get_timestamp(column_date[index_of_separator+3:])
                    while timestamp<timestamp2:
                        update_report(candidat, value, timestamp, regionName)
                        timestamp = timestamp + 60*60*24*1000
            if in_region:
                region = regions[row[0]]['name']
                value = int(row[1])
                print(region)
                candidat['geoReport'][region] = value
            elif len(row)>0 and row[0] == "Sous-région":
                in_region = True
        i=i+1
    db.candidat.update({"_id": candidat['_id']}, candidat)    
    
    
    
connector = pyGTrends('avricot.team','table87table')
connection = Connection('serv.avricot.com', 27017)
db = connection.prediction
for candidat in db.candidat.find({}):
    if candidat['candidatName'] == "SARKOZY":
        sarkozy_query = build_query(candidat)
    if candidat['candidatName'] == "HOLLANDE":
        hollande_query = build_query(candidat)        
        
j=0
for candidat in db.candidat.find({}):
    for region in regions:
        if j>1:
            #break
            time.sleep(15)
        j=j+1
        queries = [sarkozy_query, hollande_query]
        queries.append(build_query(candidat))
        raw_data = connector.download_report(queries, geo=regions[region]['id']) #regions[region]['id'])
        handle_data(raw_data, candidat, regions[region]['name'])
        
    time.sleep(15)
    
    queries = [sarkozy_query, hollande_query]
    queries.append(build_query(candidat))
    print(queries)
    raw_data = connector.download_report(queries)
    handle_data(raw_data, candidat)
#print connector.csv()


