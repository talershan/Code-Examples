from .Die import Dice
from collections import Counter

class Player:
    def __init__(self):
        self.hand = []  # List of Dice objects
        self.doubloons = 0  # Player's current doubloons

    def generate_hand(self, dice_count=7):
        # Create a new hand of dice
        self.hand = [Dice() for _ in range(dice_count)]
        for die in self.hand:
            die.roll()
        return self.hand

    def display_hand(self):
        # Show the value and color of each die
        return [(die.value, die.color) for die in self.hand]

