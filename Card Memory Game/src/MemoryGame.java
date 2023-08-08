import javax.swing.*;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MemoryGame extends JFrame
{
    JPanel panel = new JPanel();                // Create a panel to hold buttons
    int numMoves = 0;                           //Tracks the number of moves made by the user
    Card previousPreviousCard = new Card();     //Tracks the card three moves ago
    Card previousCard = new Card();             //Tracks the card two moves ago (one move ago would be CURRENT card)

    public class Card
    {
        private ImageIcon back = new ImageIcon("card/b1fv.png");    //Back of card
        private ImageIcon face = new ImageIcon();                           //Face of card
        private JButton cardButton = new JButton();                         //Button for card
        private boolean isFaceUp = false;                                   //Checks if card is face up

        //Constructors
        public Card(){}
        public Card(ImageIcon face) {
            this.face = face;
            this.cardButton = new JButton(back);
        }

            //Getters & Setters
        public ImageIcon getFace() {
            return face;
        }
        public JButton getCardButton() { return cardButton; }

        public void setFaceDown() {
            cardButton.setIcon(back);
            isFaceUp = false;
        }

        public void setFaceUp() {
            cardButton.setIcon(face);
            isFaceUp = true;
        }
    }

    public Card[] CreateDeck(int size)
    {
        if (size > 108)     //Max size is 108 (there are 54 distinct cards)
        {
            System.out.print("Size cannot be larger than 108");
            return null;
        }

        int[] nums;
        int ind = 0;
        int min = 1;
        nums = new int[54];
        Card[] deck = new Card[size];

        for (int i = 0; i < nums.length; i++)
            nums[i] = i;

            //Takes size divided by 2 and gets that number of deck from files, then makes a pair of each card
        for (int i = 0; i < size/2; i++)
        {
            int randomNum = new Random().nextInt(size-min) + min;                          //Picks a random number from 1 to size of deck
            ImageIcon cardFace = new ImageIcon("card/" + nums[randomNum] + ".png");       //Gets card images from file of sequentially named deck
            deck[ind++] = new Card(cardFace);
            deck[ind++] = new Card(cardFace);

            //Swaps value at a random index with minimum index in nums array
            int temp = nums[min];
            nums[min] = nums[randomNum];
            nums[randomNum] = temp;

            min++;     //Increase min to exclude already used values from nums array
        }
        return deck;
    }

    //Durstenfeld shuffle
    public Card[] ShuffleDeck(Card[] deck)
    {
        Random rnd = ThreadLocalRandom.current();
        for (int i = deck.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);

            // Simple swap
            Card temp = deck[index];
            deck[index] = deck[i];
            deck[i] = temp;
        }
        return deck;
    }
    public MemoryGame()
    {
        Card[] unshuffledDeck = CreateDeck(18);      //Creates a deck of cards of a specific size
        Card[] deck = ShuffleDeck(unshuffledDeck);       //Shuffles deck
        JButton youWin = new JButton("                                              ");
        Clicker clicker = new Clicker(deck, youWin);     //Create a clicker
        add(panel);                                      // Add panel to the frame

            //Adds the cards to the panel and an action listener clicker to each card button
        for(int i = 0; i < deck.length; i++)
        {
            deck[i].getCardButton().addActionListener(clicker);
            panel.add(deck[i].cardButton);
        }
        panel.add(youWin); //Add an extra panel to display when you win the game
    }

    public static void main(String[] args)
    {
        JFrame frame = new MemoryGame();
        frame.setTitle("Handle Event");
        frame.setSize(700, 500);
        frame.setLocation(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class Clicker implements ActionListener
    {
        Card[] deck;
        JButton youWin;
        public Clicker(Card[] deck, JButton youWin) {
            this.deck = deck;
            this.youWin = youWin;
        }

        //Checks if the game has been won and display a message with player's number of moves
        public void checkIfGameWon()
        {
            boolean win = true;
            for(int i = 0; i < deck.length; i++)    //Loops through deck
                if (!deck[i].isFaceUp)              //Check if card is NOT face up
                {
                    win = false;                    //Player has not won if every card is NOT face up
                    break;
                }

            //Sets text to a button to display message & number of moves
            if(win == true)
            {
                youWin.setText("                         YOU WON IN " + numMoves + " MOVES!                         ");
                youWin.addActionListener(this);
                System.out.println("Congratulations! You have won Memory Game in " + numMoves + " moves!");
            }
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            Card currentCard = new Card();                     //Create temp variable to hold the current clicked card

            for (int i = 0; i < deck.length; i++)              //Loops through all cards in the deck
                if (e.getSource() == deck[i].getCardButton())  //Checks if the button the user clicks matches the button of a card
                    if (!deck[i].isFaceUp)                     //Checks if card is not face up
                    {
                        deck[i].setFaceUp();                   //Sets card to face up
                        deck[i].isFaceUp = true;
                        numMoves++;                            //Increment the move counter
                        currentCard = deck[i];                 //Store the card the user clicked into currentCard temp variable
                    }
                    else return;                               //Break out of for loop if user clicks a card that is face up because it is not a valid move

            if (numMoves > 2 && numMoves % 2 == 1)                             //Checks if user is on their third move
                if (previousPreviousCard.getFace() != previousCard.getFace())  //Checks if the two previous moves were different cards and sets them both face down if they are different
                {
                    previousPreviousCard.setFaceDown();
                    previousCard.setFaceDown();
                }

                //Keep track of cards to check if they match later
            if (previousCard != null)
                previousPreviousCard = previousCard;
            previousCard = currentCard;

            checkIfGameWon();
            if(e.getSource() == youWin)     //If game is won, click the "YOU WIN" button to exit game
            {
                System.exit(0);
            }
        }
    }
}