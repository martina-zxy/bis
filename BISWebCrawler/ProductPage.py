from bs4 import BeautifulSoup
import requests
import time
from entity.book import Book
from entity.user import Reviewer

def get_book(url):
    headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' # don't remove!
    }

    pageNb = 1;
    urlTemp = url + "/ref=cm_cr_getr_d_show_all?pageNumber=" + str(pageNb) + "&pageSize=50&reviewerType=all_reviews" #max page size = 50

    wb_data = requests.get(urlTemp, headers=headers)

    time.sleep(1)
    soup = BeautifulSoup(wb_data.text,'lxml')
    booktitles=soup.select('div > div.a-fixed-left-grid-col.a-col-right > div > div > div.a-fixed-left-grid-col.product-info.a-col-right > div.a-row.product-title > h1 > a')

    title = booktitles[0].get_text()
    book = Book(None, None, None, title)
    list_book_review = get_review(soup)

    paging = soup.select('ul.a-pagination > li.a-last > a')

    while len(paging) > 0:
        pageNb += 1
        urlTemp = url + "/ref=cm_cr_getr_d_show_all?pageNumber=" + str(pageNb) + "&pageSize=50&reviewerType=all_reviews"
        print(str(pageNb) + ":" + urlTemp)
        wb_data = requests.get(urlTemp, headers=headers)
        soup = BeautifulSoup(wb_data.text, 'lxml')
        list_book_review.extend(get_review(soup))
        paging = soup.select('ul.a-pagination > li.a-last > a')

    book.listCustReview = list_book_review;
    return book


def get_review(soup):
    list_review = []

    reviews = soup.select('span[data-hook="review-body"]')
    review_authors = soup.select('a[data-hook="review-author"]')
    stars = soup.select('i[data-hook="review-star-rating"] > span[class="a-icon-alt"]')

    for reviewAuthor, star, review in zip(review_authors, stars, reviews):
        review.author = reviewAuthor.get_text()
        review.authorLink = reviewAuthor.get('href')
        review.star = star.get_text()
        review.content = review.get_text()
        list_review.append(review)

    return list_review
