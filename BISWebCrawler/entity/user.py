class Reviewer:

    def __init__(self, id):
        self.id = id
        self.name = ''
        self.country = ''
        self.nbHelpfulVotes = 0
        self.rank = 0
        self.profileText = ''
        self.picture = ''
        self.following = None

class Author:
    def __init__(self, id):
        self.id = id
        self.name = ''
        self.biography = ''
        self.rank = 0
        self.link = ''
        self.is_registered = 0
        self.type = ''


