# WikiExtrator
Extractor for wikipedia xml dump files.
For each wikipedia article, its title, infoboxes, categories, langlinks, pageid, abstract, etc.. are extracted from a dump file and written in separate .dat files.

# How to use
* The wikipedia dump files are very large and must be downloaded separately from "dumps.wikimedia.org" or from the Keg server (path : "10.1.1.66:/data/dump/")
example of dump file :
FR : "frwiki-20161120-pages-articles-multistream.xml"
ZH : "zhwiki-20160203-pages-articles.xml"
* from src/main/Preprocess.java -> main() un-comment or comment the languages / files that need to be extracted 
