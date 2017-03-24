from bs4 import BeautifulSoup
import requests
import time
from entity.book import Book
from entity.book import Review


def get_book(url, urlReview):
    headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' # don't remove!
    }

    wb_data = requests.get(url, headers=headers)
    # time.sleep(1)
    soup = BeautifulSoup(wb_data.text,'lxml')

    # parse book data: title
    booktitles=soup.select('div > div.a-fixed-left-grid-col.a-col-right > div > div > div.a-fixed-left-grid-col.product-info.a-col-right > div.a-row.product-title > h1 > a')
    booktitle = soup.find(id = "productTitle")
    title = booktitle.get_text()
    book = Book(None, None, None, title)
    # end parse book data: title

    # parse book data: star
    tbl_review = soup.find(id = "histogramTable")

    perc5Star = tbl_review.find(attrs={'class': ['a-size-base a-link-normal 5star histogram-review-count']}).get_text().replace('%','')
    perc4Star = tbl_review.find(attrs={'class': ['a-size-base a-link-normal 4star histogram-review-count']}).get_text().replace('%','')
    perc3Star = tbl_review.find(attrs={'class': ['a-size-base a-link-normal 3star histogram-review-count']}).get_text().replace('%','')
    perc2Star = tbl_review.find(attrs={'class': ['a-size-base a-link-normal 2star histogram-review-count']}).get_text().replace('%','')
    perc1Star = tbl_review.find(attrs={'class': ['a-size-base a-link-normal 1star histogram-review-count']}).get_text().replace('%','')
    book.set_star_percentage(perc5Star, perc4Star, perc3Star, perc2Star, perc1Star)
    # end parse book data: star

    # parse book data : avgcustreview
    rating = soup.find('span', attrs={'class', 'arp-rating-out-of-text'}).get_text()
    rating = float(rating.partition(' ')[0])
    book.avgCustReview = rating
    # end parse book data : avgcustreview

    # get review data
    pageNb = 1;
    urlTemp = urlReview + "/ref=cm_cr_getr_d_show_all?pageNumber=" + str(pageNb) + "&pageSize=50&reviewerType=all_reviews"  # max page size = 50
    wb_data_review = requests.get(urlTemp, headers=headers)

    # time.sleep(1)
    soupReview = BeautifulSoup(wb_data_review.text, 'lxml')

    # parse book data: review
    list_book_review = get_review(soupReview)
    paging = soupReview.select('ul.a-pagination > li.a-last > a')

    while len(paging) > 0:
        pageNb += 1
        urlTemp = urlReview + "/ref=cm_cr_getr_d_show_all?pageNumber=" + str(pageNb) + "&pageSize=50&reviewerType=all_reviews"
        print(str(pageNb) + ":" + urlTemp)
        wb_data = requests.get(urlTemp, headers=headers)
        soup = BeautifulSoup(wb_data.text, 'lxml')
        list_book_review.extend(get_review(soup))
        paging = soup.select('ul.a-pagination > li.a-last > a')
        print(len(list_book_review))
    book.listCustReview = list_book_review;
    book.nbCustReview = len(list_book_review)
    # end parse book data: review

    # parse book data : summary
    # parse book data : language
    # parse book data : authorid
    # parse book data : bookrank
    # parse book data : publisher
    # parse book data : publicationdate

    return book


def get_review(soup):
    list_review = []
    ASIN = '0544227751'
    reviews = soup.select('div[data-hook="review"]')
    for r in reviews:
        review = Review('0544227751')

        # title
        title = r.find('a', attrs={'data-hook': 'review-title'}).get_text()
        review.title = title

        # star
        star = r.find('i', attrs = {'data-hook':'review-star-rating'}).get_text();
        review.nbStar = float(star.partition(' ')[0])

        # author and author link
        dom_author = r.find('a', attrs = {'data-hook':'review-author'})
        author_link = dom_author.get('href')
        author = dom_author.get_text();
        review.reviewerId = author

        # is verified
        is_verified =  r.find('span', attrs = {'data-hook':'avp-badge'})
        if is_verified is None:
            is_verified = False
        else:
            is_verified = True
        review.verifiedPurchase = is_verified

        # review text
        review.reviewText = r.find('span', attrs = {'data-hook':'review-body'}).get_text()

        # review date
        date = r.find('span', attrs = {'data-hook':'review-date'}).get_text()
        review.date = date.replace('on ', '')

        # helpful
        helpfulText = r.find('span', attrs={'data-hook': 'review-voting-widget'}).get_text()
        nbHelpful = helpfulText.strip().partition(' ')[0]

        if nbHelpful == "One":
            nbHelpful = 1
        elif not nbHelpful.isdigit():
            nbHelpful = 0
        else:
            nbHelpful = int(nbHelpful)
        review.nbHelpful = nbHelpful

        # votes??

        list_review.append(review)


    return list_review
