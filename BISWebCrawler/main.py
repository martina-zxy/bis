import ProductPage
import AuthorPage
import db_conn
import ReviewerPage
from entity.user import Author
from datetime import datetime
import sys
import time

url_review = 'https://www.amazon.com/Big-Data-Revolution-Transform-Think/product-reviews/0544227751'
url = 'https://www.amazon.com/Big-Data-Revolution-Transform-Think/dp/0544227751'

url_review = 'https://www.amazon.com/Hunger-Games-Book-1/product-reviews/0439023521/'
url = 'https://www.amazon.com/Hunger-Games-Book-1/dp/0439023521'
# url = 'https://www.amazon.com/Big-Little-Lies-Liane-Moriarty/dp/0425274861'
# url = 'https://www.amazon.com/Mangrove-Lightning-Doc-Ford-Novel-ebook/dp/B01HYUVHGY/ref=cm_rdp_product'
asin = 'B06XQCKG2Z'
url = "https://www.amazon.com/Becoming-Robin-Tale-Self-Discovery-ebook/dp/B06XQCKG2Z/ref=zg_bs_10368555011_49/141-6728850-2537519?_encoding=UTF8&psc=1&refRID=4KQ2WHHYM5PPPVMEJJPP"
url_review = "https://www.amazon.com/product-reviews/B06XQCKG2Z"

# asin = '1119145678'
# url = "https://www.amazon.com/Predictive-Analytics-Power-Predict-Click/dp/1119145678/ref=pd_bxgy_14_img_2?_encoding=UTF8&pd_rd_i=1119145678&pd_rd_r=P51PE6AD1F082RZAJRD2&pd_rd_w=7bgmR&pd_rd_wg=lfK5P&psc=1&refRID=P51PE6AD1F082RZAJRD2"
# url_review = "https://www.amazon.com/product-reviews/1119145678"

books_to_crawl = db_conn.get_best_seller_to_crawl(4)
for book_data in books_to_crawl:
    print(book_data[0], book_data[1], book_data[3])

    asin = book_data[0]
    url = book_data[1]
    url_review = "https://www.amazon.com/product-reviews/" + asin
    print(asin)
    print(url)
    print(url_review)

    # if db_conn.check_book(asin):
    #     # db_conn.update_best_seller_crawled(asin)
    #     # print("!!CRAWLED!!")
    #     # sys.exit()
    # else:

    # 1. Crawl book
    book = ProductPage.get_book(asin, url, url_review)

    data_book = {
            'asin':book.asin,
            'language':book.language,
            'avgCustReview' : book.avgCustReview,
            'nbCustReview' : book.nbCustReview,
            'percFiveStar' : book.percFiveStar,
            'percFourStar' : book.percFourStar,
            'percThreeStar' : book.percThreeStar,
            'percTwoStar' : book.percTwoStar,
            'percOneStar' : book.percOneStar,
            'rank' : book.rank,
            'publisher' : book.publisher,
            'publicationDate' : book.publicationDate
            }

    # 2. Insert author
    list_author = []
    for author in book.listAuthor:
        print(author.id, author.name, author.link, author.biography, author.rank)
        if not db_conn.check_author(author.id):
            db_conn.insert_author(author)

    # 3. Insert book
    if not db_conn.check_book(book.asin):
        db_conn.insert_book(book)
    print(data_book)

    # 5. Insert to AuthorBook
    for author in book.listAuthor:
        if not db_conn.check_authorbook(author.id, book.asin):
            print(book.asin, author.id)
            db_conn.insert_authorbook(author.id, book.asin)

    # 5. Insert FBT, ABT
    for bookStr in book.listFBT:
        print(bookStr)
        if not db_conn.check_booktocrawl(bookStr) and not db_conn.check_book(bookStr):
            db_conn.insert_booktocrawl(bookStr,None,'fbt')

        if not db_conn.check_frequentlyboughttogether(book.asin, bookStr):
            db_conn.insert_frequentlyboughttogether(book.asin, bookStr)

    for bookStr in book.listAB:
        print(bookStr)
        if not db_conn.check_booktocrawl(bookStr) and not db_conn.check_book(bookStr) :
            db_conn.insert_booktocrawl(bookStr, None,'ab')

        if not db_conn.check_alsobought(book.asin, bookStr):
            db_conn.insert_alsobought(book.asin, bookStr)
    # 6. Crawl Customer Review

    # 7. Insert Customer Review
    for review in book.listCustReview:
        data_review = {
            'id': review.id,
            'asin':review.asin,
            'nbStar' : review.nbStar,
            'title' : review.title,
            'date' : review.date,
            'reviewerId' : review.reviewerId,
            'reviewerName' : review.reviewerName,
            'verifiedPurchase' : review.verifiedPurchase,
            'nbHelpful': review.nbHelpful,
            'nbVotes': review.nbVotes,
            'text' : review.reviewText,
            'nbComments' : review.nbComments,
            'reviewer_profile' : review.reviewerLink
            }
        print(data_review)
        if not db_conn.check_customerreview(review.id):
            db_conn.insert_review(review)

    # 8. Crawl Reviewer
    for review in book.listCustReview:
        if not db_conn.check_reviewer(review.reviewerId):
            print("Reviewer :" , review.reviewerLink)

            if db_conn.check_reviewer(review.reviewerId):
                continue

            time.sleep(5)

            reviewer = ReviewerPage.get_reviewer(review.reviewerLink)
            if reviewer is not None:
                data_reviewer = {
                    'name' : reviewer.name,
                    'country' : reviewer.country,
                    'nbHelpfulVotes' : reviewer.nbHelpfulVotes,
                    'rank' : reviewer.rank,
                    'profileText' : reviewer.profileText
                }
                print(data_reviewer)

                # 9. Insert Reviewer
                db_conn.insert_reviewer(reviewer)
                for x in reviewer.following:
                    print("Type : ", x.type)
                    if x.type == "author":
                        if not db_conn.check_author(x.id) and not db_conn.check_authortocrawl(x.id):
                            db_conn.insert_authortocrawl(x.id, x.link,'rvwr')

                            au = Author(x.id)
                            au.id = x.id
                            au.link = x.link
                            au.name = x.name
                            db_conn.insert_author(au)

                        # 9. Insert ReviewerFollowing
                        if not db_conn.check_following(reviewer.id, x.id):
                            db_conn.insert_reviewerfollowing(reviewer.id, x.id)

                        data_author = {
                            'following_id' : x.id,
                            'following_name' : x.name,
                            'following_link' : x.link,
                            'type': x.type
                        }
                        print(data_author)
                    else :
                        if not db_conn.check_reviewer(x.id) and not db_conn.check_followed_customer(x.id):
                            db_conn.insert_followedcustomer(x.id, x.link, x.name)

                        # 10. Insert ReviewerFollowing
                        if not db_conn.check_followingcustomer(reviewer.id, x.id) :
                            db_conn.insert_reviewerfollowingcustomer(reviewer.id, x.id)

                        data_author = {
                            'following_id': x.id,
                            'following_name': x.name,
                            'following_link': x.link,
                            'type':x.type
                        }
                        print(data_author)

        # 11. Insert Comment
        for comment in review.listComment:
            if not db_conn.check_comment(comment.id):
                db_conn.insert_comment(comment)

    db_conn.update_best_seller_crawled(book.asin)