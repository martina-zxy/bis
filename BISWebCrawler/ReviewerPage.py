from bs4 import BeautifulSoup
import requests
import time
from entity.book import Book
from entity.user import Reviewer

url = 'https://www.amazon.com/gp/profile/amzn1.account.AH5KNRSD6RKBCN4F7WT6TPIYEUGA'

def get_reviewer(url,data=None):
    #max page size = 50
    headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' #don't remove!
    }

    wb_data = requests.get(url, headers=headers)

    time.sleep(1)
    soup = BeautifulSoup(wb_data.text,'lxml')
    reviewer = Reviewer('AH5KNRSD6RKBCN4F7WT6TPIYEUGA');
    name = soup.find('span', attrs = {'class':'public-name-text'}).get_text().strip();
    locationOccupation = soup.find('div', attrs={'class' : 'a-fixed-right-grid location-and-occupation-holder'}).get_text().strip()

    profile = soup.find('span',  attrs={'class': 'a-size-base location-and-bio-text'}).get_text().strip()
    bio = soup.find('div', attrs={'class' : 'bio-expander'})
    trace = bio.select('div > div > div')

    nbHelpful = trace[2].get_text().replace(",",'').strip()
    print()
    rank = trace[3].find('div').find('div').get_text().replace("#","").strip()
    print()
    # print(trace[4].prettify())
    print(name)
    print(locationOccupation)
    print(profile)
    print(nbHelpful)
    print(rank)

    reviewer.name = name
    reviewer.country = locationOccupation
    reviewer.profileText = profile
    reviewer.nbHelpfulVotes = nbHelpful
    reviewer.rank = rank

    # there is no way to check if the user uses a real profile picture only from the link

get_reviewer(url);