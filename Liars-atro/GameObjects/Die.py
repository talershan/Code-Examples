import random

class Dice:
    def __init__(self, sides=6):
        self.sides = sides
        self.value = 1
        self.color = random.choice(["yellow", "red", "blue", "black"])  # Replace green with yellow

    def roll(self):
        self.value = random.randint(1, self.sides)
        self.color = random.choice(["yellow", "red", "blue", "black"])  # Randomize color on roll
        return self.value
