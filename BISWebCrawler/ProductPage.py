from bs4 import BeautifulSoup
import requests
import time

url_saves = 'http://www.tripadvisor.com/Saves#37685322'
url = 'https://www.amazon.com/Big-Data-Revolution-Transform-Think/product-reviews/0544227751'
urls = ['https://cn.tripadvisor.com/Attractions-g60763-Activities-oa{}-New_York_City_New_York.html#ATTRACTION_LIST'.format(str(i)) for i in range(30,930,30)]

headers = {
    'User-Agent':'',
    'Cookie':''
}


def get_attractions(url,data=None):
    wb_data = requests.get(url)
    time.sleep(1)
    soup = BeautifulSoup(wb_data.text,'lxml')
    booktitles=soup.select('div > div.a-fixed-left-grid-col.a-col-right > div > div > div.a-fixed-left-grid-col.product-info.a-col-right > div.a-row.product-title > h1 > a')
    reviews =soup.select('span[data-hook="review-body"]')
    reviewAuthors =soup.select('a[data-hook="review-author"]')
    stars= soup.select('i[data-hook="review-star-rating"] > span[class="a-icon-alt"]')

    print("booktitle:",booktitles[0].get_text())
    if data == None:
        for reviewAuthor,star,review in zip(reviewAuthors,stars,reviews):
            data = {
                'author':reviewAuthor.get_text(),
                'authorlink':reviewAuthor.get('href'),
                'star': star.get_text(),
                'review':review.get_text()

                }
            print(data)




# for single_url in urls:
#     get_attractions(single_url)

get_attractions(url)