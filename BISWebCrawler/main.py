import ProductPage

url = 'https://www.amazon.com/Big-Data-Revolution-Transform-Think/product-reviews/0544227751'

book = ProductPage.get_book(url)
print("main")
print(book.author)
for review in book.listCustReview:
    print(review.author, ' : ', review.authorLink)
