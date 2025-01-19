import tkinter as tk
from GameObjects.Player import Player
from GameObjects.Die import Dice

class DiceGame:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("Dice Game")
        self.root.geometry("650x350")

        self.hand = [Dice() for _ in range(7)]  # Initialize 7 dice
        self.selected_indices = []  # Track selected dice
        self.doubloons = 0

        # Frames
        self.menu_frame = None
        self.game_frame = None

        # UI elements
        self.dice_buttons = []
        self.info_label = None
        self.hand_type_label = None

        self.create_menu()

    def create_menu(self):
        """Create the start-up menu."""
        self.menu_frame = tk.Frame(self.root)
        self.menu_frame.pack(fill="both", expand=True)

        title_label = tk.Label(self.menu_frame, text="Welcome to Dice Game!", font=("Helvetica", 24))
        title_label.pack(pady=20)

        start_button = tk.Button(self.menu_frame, text="Start Game", font=("Helvetica", 16), command=self.start_game)
        start_button.pack(pady=10)

        exit_button = tk.Button(self.menu_frame, text="Exit", font=("Helvetica", 16), command=self.root.quit)
        exit_button.pack(pady=10)

    def start_game(self):
        """Transition from the menu to the game UI."""
        self.menu_frame.destroy()  # Remove the menu frame
        self.create_game_ui()

    def create_game_ui(self):
        """Create the main game UI with reorganized layout."""
        self.game_frame = tk.Frame(self.root)
        self.game_frame.pack(fill="both", expand=True)

        # Selected dice display area
        self.selected_area = tk.Frame(self.game_frame, pady=10)
        self.selected_area.pack(fill="x", pady=5)

        # Hand type and doubloons display
        self.info_area = tk.Frame(self.game_frame, pady=10)
        self.info_area.pack(fill="x")

        self.hand_type_label = tk.Label(self.info_area, text="Hand Type: None", font=("Helvetica", 14), fg="blue")
        self.hand_type_label.pack()

        self.info_label = tk.Label(self.info_area, text=f"Doubloons: {self.doubloons}", font=("Helvetica", 14))
        self.info_label.pack()

        # Dice buttons area
        self.dice_area = tk.Frame(self.game_frame, pady=20)
        self.dice_area.pack(side="bottom", fill="x")

        # Create dice buttons
        for i in range(len(self.hand)):
            dice_button = tk.Button(
                self.dice_area,
                text="?",
                font=("Helvetica", 16),
                width=5,
                height=2,
                command=lambda idx=i: self.toggle_select(idx),
            )
            dice_button.grid(row=0, column=i, padx=10)
            self.dice_buttons.append(dice_button)

        # Submit button
        self.submit_button = tk.Button(self.dice_area, text="Submit Selected", font=("Helvetica", 14), command=self.submit_dice)
        self.submit_button.grid(row=1, columnspan=7, pady=10)

        self.roll_hand()  # Initial roll
        self.update_ui()


    def toggle_select(self, idx):
        """Toggle dice selection and update the hand type display."""
        if idx in self.selected_indices:
            self.selected_indices.remove(idx)
            self.update_ui()
        else:
            if len(self.selected_indices) >= 5:
                self.hand_type_label.config(text="You can only select up to 5 dice.")
                return
            self.dice_buttons[idx].config(bg="#008000")  # Highlight with green
            self.selected_indices.append(idx)

        # Update the hand type and selected dice display
        self.update_hand_type()
        self.update_selected_dice_display()


    def roll_hand(self):
        """Roll all dice."""
        for die in self.hand:
            die.roll()

    def update_ui(self):
        """Update dice buttons to reflect the current hand."""
        color_mapping = {
            "yellow": "#FFD700",
            "red": "#FF0000",
            "blue": "#0000FF",
            "black": "#000000",
        }
        for i, die in enumerate(self.hand):
            self.dice_buttons[i].config(
                text=str(die.value),
                bg=color_mapping[die.color],
                fg="white" if die.color == "black" else "black",
            )

    def update_hand_type(self):
        """Update the hand type display based on the selected dice."""
        if not self.selected_indices:
            self.hand_type_label.config(text="Hand Type: None")
            return

        selected_dice = [self.hand[i] for i in self.selected_indices]
        hand_type, reward = self.calculate_hand_type(selected_dice)

        # Display the hand type and potential reward
        self.hand_type_label.config(text=f"Hand Type: {hand_type}, Potential Reward: {reward} doubloons")

    def submit_dice(self):
        """Submit the selected dice, reroll them, and update the UI."""
        if not self.selected_indices:
            self.hand_type_label.config(text="No dice selected to submit.")
            return

        # Get the selected and non-selected dice
        non_selected_dice = [self.hand[i] for i in range(len(self.hand)) if i not in self.selected_indices]
        selected_dice = [self.hand[i] for i in self.selected_indices]

        # Calculate the hand type and reward
        hand_type, reward = self.calculate_hand_type(selected_dice)
        self.doubloons += reward

        # Reroll the selected dice
        for die in selected_dice:
            die.roll()

        # Reorganize the hand
        self.hand = non_selected_dice + selected_dice

        # Update the UI
        self.info_label.config(text=f"Doubloons: {self.doubloons}")
        self.selected_indices = []
        self.update_ui()
        self.update_selected_dice_display()

        # Display the submitted hand and reward
        self.hand_type_label.config(text=f"Submitted Hand: {hand_type}, Reward Earned: {reward} doubloons")




    def calculate_hand_type(self, dice):
        """
        Determines the type of hand and calculates the corresponding reward for the given dice.
        """
        values = sorted([die.value for die in dice])
        colors = [die.color for die in dice]
        n = len(values)

        # Flush Check: All dice must have the same color
        if len(set(colors)) == 1 and n == 5:  # All same color and exactly 5 dice
            return "Flush", 1200

        # Hand type checks
        if n == 5 and values[0] == values[4]:  # Five of a Kind
            return "Five of a Kind", 1000
        elif n >= 4 and ((n > 3 and values[0] == values[3]) or (n > 4 and values[1] == values[4])):  # Four of a Kind
            return "Four of a Kind", 800
        elif n == 5 and (
            (values[0] == values[2] and values[3] == values[4]) or (values[0] == values[1] and values[2] == values[4])
        ):  # Full House
            return "Full House", 900
        elif n >= 3 and (
            (n > 2 and values[0] == values[2]) or (n > 3 and values[1] == values[3]) or (n == 5 and values[2] == values[4])
        ):  # Three of a Kind
            return "Three of a Kind", 600
        elif n >= 4:
            pairs = 0
            skip = -1
            for i in range(n - 1):  # Count distinct pairs
                if i == skip:
                    continue
                if values[i] == values[i + 1]:
                    pairs += 1
                    skip = i + 1
            if pairs == 2:
                return "Two Pair", 400
        elif n >= 2 and any(values[i] == values[i + 1] for i in range(n - 1)):  # Pair
            return "Pair", 200

        return "No Hand", 0

    
    def update_selected_dice_display(self):
        """Update the selected dice display area with unpressable squares."""
        for widget in self.selected_area.winfo_children():
            widget.destroy()  # Clear previous selected dice display

        if not self.selected_indices:
            placeholder = tk.Label(self.selected_area, text="No dice selected", font=("Helvetica", 14), fg="blue")
            placeholder.pack()
            return

        color_mapping = {
            "yellow": "#FFD700",
            "red": "#FF0000",
            "blue": "#0000FF",
            "black": "#000000",
        }

        for i in self.selected_indices:
            die = self.hand[i]
            die_label = tk.Label(
                self.selected_area,
                text=str(die.value),
                font=("Helvetica", 16),
                width=5,
                height=2,
                bg=color_mapping[die.color],
                fg="white" if die.color == "black" else "black",
                relief="solid",
                bd=2
            )
            die_label.pack(side="left", padx=5)



    def run(self):
        """Run the main application loop."""
        self.root.mainloop()

if __name__ == "__main__":
    game = DiceGame()
    game.run()
