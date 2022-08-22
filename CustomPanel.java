//Steven Lynch 8/21/2022
//Binary Adder

//AWT Advanced Windowing Toolkit
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
//          ^^^package
//				^^^class
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

//~ import java.awt.Font;
import java.awt.*;



class CustomPanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener {
    // ALL INFORMATION FOR WHAT CURRENT STATE IS SHOULD BE STORED HERE, E.G. BACKGROUND COLOR, BLOCK SPACES,
    // STORED (BUT MODIFIABLE) STATE
    Dimension housingRectSizePerDigit = new Dimension(100, 175);
    public int numOfBinNumListsBeingAdded = 2;
    //CHANGE BELOW #S TO ALTER LENGTH OF ADDED NUMS (e.g. change to 28 to have a 28-bit #)
    public int numOne_numOfBinDigitsPerBaseTenNumber = 8; // #numsThatCanBeStored: 2^8=256, maxNumThatCanBeStored: (0-255)
    public int numTwo_numOfBinDigitsPerBaseTenNumber = 8; // #numsThatCanBeStored: 2^8=256, maxNumThatCanBeStored: (0-255)
    
    public int[] fullNumOneDigitsList = new int[numOne_numOfBinDigitsPerBaseTenNumber];
    public int[] fullNumTwoDigitsList = new int[numTwo_numOfBinDigitsPerBaseTenNumber];
    public int[] addedDigitsList;
    private final int numOfOverflowAndCarryDigits = 1;
    //I originally had two extra bits, but the MSB (with 2 extra bits) can never be 1 (experimentally proven), even
    //  with every bit set to 1

    public boolean numOneHasMoreDigitsThanNumTwo = numOne_numOfBinDigitsPerBaseTenNumber > numTwo_numOfBinDigitsPerBaseTenNumber;
    public int greatestNumOfDigits;
    
    private int carry = 0;

    public int mouseX, mouseY;
    public int marginScale = 1; // This scale changes both margins by a scalar
    public int lineSpacingScale = 1; // This scale changes both line spacings by a scalar
    public int xLineSpacing = (int) (lineSpacingScale * 50); // Okayyyyyy so don't mention getHeight() or getWidth() on
                                                             // these 4 because they==0... Why do they==0?
    public int yLineSpacing = (int) (lineSpacingScale * (35 + getHeight() * .125));
    public int xMarginOffset = (int) (marginScale * (60 + getWidth() * .5));
    public int yMarginOffset = (int) (marginScale * (75 + getHeight() * .04));

    private static final int EMPTY_BOX = -1000;
    private static final int DIGIT_ZERO = 0;
    private static final int DIGIT_ONE = 1;
    private static final Color c1 = Color.RED;
    private static final Color c2 = new Color(83, 229, 160);
    private static final Color c3 = new Color(0x00FCFF);
    private boolean sectionOfNumOneIsHoveredOver;
    private boolean sectionOfNumTwoIsHoveredOver;

    public void print(String s) // Helper function so I don't take an entire line to call the print function
        {System.out.print(s);}
    public void printl(String s) // Helper function so I don't take an entire line to call the print function
        {System.out.println(s);}

    private Graphics2D graphics2DSetup(Graphics g)
    {
        // Graphics2D has all the functions that Graphics does, but more.
        // Most needed functions are in Graphics, not Graphics2D.
        // (Graphics2D)g: upcasting g (starts as Graphics) to g2 (with all info Graphics2D has)
        // Graphics2D is abstract?, so you can never make your own? - you have to import it?
        Graphics2D g2 = (Graphics2D) g;

        // Antialiasing:
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
        rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        return g2;
    }

    private void reset()
    {
        fullNumOneDigitsList = new int[greatestNumOfDigits];
        fullNumTwoDigitsList = new int[greatestNumOfDigits];
        for(int i=0; i<greatestNumOfDigits; i++) {
            fullNumOneDigitsList[i] = EMPTY_BOX;
            fullNumTwoDigitsList[i] = EMPTY_BOX;
        }
        System.out.println("All entered numbers have been reset.");
        repaint();
    }

    private void assignNumOne()
    {
        for(int reversedDigit=0; reversedDigit<fullNumOneDigitsList.length; reversedDigit++) {
            sectionOfNumOneIsHoveredOver =
                       mouseX >= (reversedDigit+numOfOverflowAndCarryDigits) * xLineSpacing + xMarginOffset
                    && mouseX <= (reversedDigit+numOfOverflowAndCarryDigits+1)*xLineSpacing + xMarginOffset
                    && mouseY >= yMarginOffset   &&   mouseY <= yLineSpacing + yMarginOffset;
            if(sectionOfNumOneIsHoveredOver) // if(mouseIsInBinaryDigitBox)
            {
                // ASSIGNING
                // if(mouseIsClicked && isInSpecific_reversedDigit'sBox && !=1){currBit=1}
                if(fullNumOneDigitsList[reversedDigit] == EMPTY_BOX || fullNumOneDigitsList[reversedDigit] == DIGIT_ZERO)
                    {fullNumOneDigitsList[reversedDigit] = 1;}
                else // if(mouseIsClicked && isInSpecific_reversedDigit'sBox && ==1){currBit=0}
                    {fullNumOneDigitsList[reversedDigit] = 0;}
                printl("Mouse has been clicked in Digit " + (/* fullNumOneDigitsList.length- */reversedDigit)
                        + " of NumOne. Value: " + fullNumOneDigitsList[reversedDigit]);
            }
        }
    }

    private void assignNumTwo() {
        for(int reversedDigit=0; reversedDigit<fullNumTwoDigitsList.length; reversedDigit++) {
            sectionOfNumTwoIsHoveredOver =
                       mouseX >= (reversedDigit+numOfOverflowAndCarryDigits) * xLineSpacing + xMarginOffset
                    && mouseX <= (reversedDigit+numOfOverflowAndCarryDigits+1)*xLineSpacing + xMarginOffset
                    && mouseY >= yLineSpacing + yMarginOffset  &&  mouseY <= 2*yLineSpacing + yMarginOffset;
            if(sectionOfNumTwoIsHoveredOver) // if(mouseIsInDigitBox)
            {
                // ASSIGNING
                // if(mouseIsClicked && isInHitbox && !=1){currBit=1}
                if(fullNumTwoDigitsList[reversedDigit] == EMPTY_BOX  ||  fullNumTwoDigitsList[reversedDigit] == DIGIT_ZERO)
                    {fullNumTwoDigitsList[reversedDigit] = 1;}
                else // if(mouseIsClicked && isInHitbox && ==1){currBit=0}
                    {fullNumTwoDigitsList[reversedDigit] = 0;}
                printl("Mouse has been clicked in Digit " + (/* fullNumTwoDigitsList.length- */reversedDigit)
                        + " of NumTwo. Value: " + fullNumTwoDigitsList[reversedDigit]);
            }
        }
    }

    private void assignAllNums() {
        assignNumOne();
        assignNumTwo();
    }

    // ~ private void drawPermanentToken(Graphics2D g2, int upperLeftX, int
    // upperLeftY) //This specific func was worse than useless.
    // ~ {
    // ~ Font font = new Font(/*"Arial""Ubuntu"*/"Serif", Font.PLAIN, 40);
    // ~ g2.setFont(font);
    // ~ for(int i=1;i<addedDigitsList.length-1;i++) //WILL NOT WORK FOR EVERY
    // BINNUM ADDITION BC CAN STILL GO OVER SMALLEST LENGTH #'S INDEX
    // ~ {
    // ~ g2.drawString(fullNumOneDigitsList[i]+"",
    // (int)(upperLeftX+.25*xLineSpacing), upperLeftY);
    // ~ g2.drawString(fullNumTwoDigitsList[i]+"",
    // (int)(upperLeftX+.25*xLineSpacing), upperLeftY);
    // ~ } //+.25*xLineSpacing because token is drawn from the upper left, not the
    // middle
    // ~ }
    private void drawChangeStateToken(Graphics2D g2, String token, int upperLeftX, int upperLeftY) {
        Font font = new Font(/* "Arial""Ubuntu" */"Serif", Font.PLAIN, 40);
        g2.setFont(font);
        for(int i=1; i<addedDigitsList.length; i++)
            {g2.drawString(token, (int)(upperLeftX + .3*xLineSpacing), (int)(upperLeftY - .04*yLineSpacing));}
        // +.3*xLineSpacing because token is drawn from the upper left, not the middle,
        // likewise for -.04*yLineSpacing
    }

    private void drawBoundingBoxes(Graphics2D g2) // nonstatic is fine because it's called in paint, not main
    {
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3)); // sets thiccness

        // draws equally-spaced vertical lines
        for(int i=0; i<addedDigitsList.length+1; i++)
        {
            g2.drawLine(xLineSpacing*(i) + xMarginOffset, yMarginOffset,
                        xLineSpacing*(i) + xMarginOffset, 3*yLineSpacing + yMarginOffset);
        }
        // x-values remain same for each line, so same equation for starting & ending x

        // draws equally-spaced horizontal lines
        for(int m=0; m<=numOfBinNumListsBeingAdded+1; m++)
        // ~ {g2.drawLine(m*xMarginOffset,m*yMarginOffset, (m*getWidth())-xMarginOffset,
        // (m*getHeight())-yMarginOffset);} //Nope
        // ~ {g2.drawLine(m*xMarginOffset,m*yMarginOffset, (m*getWidth())-xMarginOffset,
        // m*yMarginOffset);} //Nope.
        {
            g2.drawLine(xMarginOffset,                                       yLineSpacing*(m) + yMarginOffset,
                        xLineSpacing*addedDigitsList.length + xMarginOffset, yLineSpacing*(m) + yMarginOffset);
        }
        // y-values remain same for each line, so same equation for starting & ending y
        // Another possible interpretation[unused and doesn't apply above though]:
        //   (amtMovedRight,amtMovedDown, panelWidth-howMuchMovedRight,panelHeight-howMuchMovedDownFromTop)
    }

    private void drawBackground_AndNonFunctionalBoundingBoxes(Color bkgrndColor, Color boxColor, Graphics g, Graphics2D g2) {
        // Colors the background teal
        g.setColor(bkgrndColor);

        // (upperLeftX,upperLeftY,width,height)
        g.fillRect(0, 0, getWidth()/* of frame(big) Dimension */, getHeight()/* of frame(big)Dimension */);

        // Hitbox color-er (no actual functionality other than visual appeal)
        g.setColor(boxColor); // c1 (red) for debug
        for(int reversedDigit=0; reversedDigit<greatestNumOfDigits; reversedDigit++) {
            // Hitboxes for 1st # to be added to other
            // g.fillRect(upperLeftX,upperLeftY,width,height);
            g.fillRect(
                    // upperLeftX (progressively increases by reversedDigit's width)
                    xMarginOffset + (reversedDigit+numOfOverflowAndCarryDigits)*xLineSpacing,
                    yMarginOffset,                                  // upperLeftY (constant)
                    xLineSpacing - numOfOverflowAndCarryDigits,     // width (constant)
                    yLineSpacing - numOfOverflowAndCarryDigits);    // height of rect (constant)

            // Hitboxes for 2nd # to be added to 1st
            // g.fillRect(upperLeftX,upperLeftY,width,height);
            g.fillRect(
                    // upperLeftX (progressively increases by reversedDigit's width)
                    xMarginOffset + (reversedDigit+numOfOverflowAndCarryDigits)*xLineSpacing,
                    yMarginOffset + /* heightOf_FirstNumList */yLineSpacing,    // upperLeftY (constant)
                    xLineSpacing - numOfOverflowAndCarryDigits,                 // width (constant)
                    yLineSpacing - numOfOverflowAndCarryDigits);                // height (constant)
        }

        drawBoundingBoxes(g2);
    }

    private void drawNumOne(Graphics2D g2)
    {
        for(int reversedDigit=0; reversedDigit<fullNumOneDigitsList.length; reversedDigit++)
        {
            sectionOfNumOneIsHoveredOver =
                   mouseX >= (reversedDigit+numOfOverflowAndCarryDigits) * xLineSpacing + xMarginOffset
                && mouseX <= (reversedDigit+numOfOverflowAndCarryDigits+1)*xLineSpacing + xMarginOffset
                && mouseY >= yMarginOffset   &&   mouseY <= yLineSpacing + yMarginOffset;

            // PERMANENT DRAWING
            // if mouse NOT in specific hitbox && hitbox's reversedDigit is unassigned, draw opaque gray "0"
            // if mouse NOT in specific hitbox && ==0, draw opaque yellow "0"
            // if mouse NOT in specific hitbox && ==1, draw opaque blue "1"
            if(!sectionOfNumOneIsHoveredOver) // if(mouseIsNOTInreversedDigitBox)
            {
                if(fullNumOneDigitsList[reversedDigit] == EMPTY_BOX)        // if(box==unassigned && mouseIsNOTinHitbox){textColor = gray}
                    {g2.setColor(Color.DARK_GRAY);}
                else if(fullNumOneDigitsList[reversedDigit] == DIGIT_ZERO)  // if(box==0 && mouseIsNOTinHitbox){textColor = yellow}
                    {g2.setColor(Color.YELLOW);}
                else{g2.setColor(Color.BLUE);}                              // if(box==1 && mouseIsNOTinHitbox){textColor = blue}
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // Makes token 100% opaque

                // drawPermanentToken(g2,(reversedDigit+1)*xLineSpacing+xMarginOffset,yLineSpacing+yMarginOffset);
                // ^^^ NO THIS IS WRONG IDK Y
                drawChangeStateToken(g2, fullNumOneDigitsList[reversedDigit]==DIGIT_ONE ? "1":"0",
                        (reversedDigit+numOfOverflowAndCarryDigits)*xLineSpacing + xMarginOffset,
                        yLineSpacing + yMarginOffset);
            }

            if(sectionOfNumOneIsHoveredOver) // if(mouseIsInreversedDigitBox)
            {
                // Red Text TEMPORARY HOVERED-OVER DRAWING (indicates a changed value if the hitbox is clicked)
                g2.setColor(Color.RED); // if(mouseIs_onScreen){textColor = red}

                if(fullNumOneDigitsList[reversedDigit] == DIGIT_ONE) // if(mouseIsInHitbox && hitbox==1){seethru text in red}
                {
                    // if(mouseIs_onScreen && mouseIsInSpecificHitbox && hitbox==1){draw translucent red "0"}
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .15f)); // Makes token(text) 15% opaque
                    drawChangeStateToken(g2, "0",
                            (reversedDigit+numOfOverflowAndCarryDigits)*xLineSpacing + xMarginOffset,
                            yLineSpacing + yMarginOffset);
                    // (Graphics2D g2, String token, int upperLeftX, int upperLeftY)
                }
                else //if(fullNumOneDigitsList[reversedDigit] != DIGIT_ONE)
                {
                    // if(mouseIs_onScreen && mouseIsInSpecificHitbox && hitbox==unassigned){draw translucent red "1"}
                    // if(mouseIs_onScreen && mouseIsInSpecificHitbox && hitbox==0){draw translucent red "1"}
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .15f)); // Makes token(text) 15% opaque
                    drawChangeStateToken(g2, "1",
                            (reversedDigit+numOfOverflowAndCarryDigits)*xLineSpacing + xMarginOffset,
                            yLineSpacing + yMarginOffset);
                } // (Graphics2D g2, String token, int upperLeftX, int upperLeftY)
            }
        }
    }

    // DUPLICATE NUMONE'S CODE DIRECTLY ABOVE, BUT REPLACE NUMONE WITH NUMTWO (ACCOUNT FOR INCREASED Y)
    private void drawNumTwo(Graphics2D g2)
    {
        for(int reversedDigit=0; reversedDigit<fullNumTwoDigitsList.length; reversedDigit++)
        {
            sectionOfNumTwoIsHoveredOver =
                   mouseX >= (reversedDigit+numOfOverflowAndCarryDigits) * xLineSpacing + xMarginOffset
                && mouseX <= (reversedDigit+numOfOverflowAndCarryDigits+1)*xLineSpacing + xMarginOffset
                && mouseY >= yLineSpacing + yMarginOffset  &&  mouseY <= 2*yLineSpacing + yMarginOffset;

            // PERMANENT DRAWING
            // if mouse NOT in specific hitbox && hitbox's reversedDigit is unassigned, draw opaque gray "0"
            // if mouse NOT in specific hitbox && ==0, draw opaque yellow "0"
            // if mouse NOT in specific hitbox && ==1, draw opaque blue "1"
            if(!sectionOfNumTwoIsHoveredOver) // if(mouseIsNOTInreversedDigitBox)
            {
                if(fullNumTwoDigitsList[reversedDigit] == EMPTY_BOX)        // if(box==unassigned && mouseIsNOTinHitbox){textColor = gray}
                    {g2.setColor(Color.DARK_GRAY);}
                else if(fullNumTwoDigitsList[reversedDigit] == DIGIT_ZERO)  // if(box==0 && mouseIsNOTinHitbox){textColor = yellow}
                    {g2.setColor(Color.YELLOW);}
                else{g2.setColor(Color.BLUE);}                              // if(box==1 && mouseIsNOTinHitbox){textColor = blue}
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // Makes token 100% opaque

                // drawPermanentToken(g2, (reversedDigit+1)*xLineSpacing+xMarginOffset,2*yLineSpacing+yMarginOffset);
                // ^^^ NO THIS IS WRONG IDK Y
                drawChangeStateToken(g2, fullNumTwoDigitsList[reversedDigit]==DIGIT_ONE ? "1":"0",
                        (reversedDigit+numOfOverflowAndCarryDigits)*xLineSpacing + xMarginOffset,
                        2*yLineSpacing + yMarginOffset);
            }
            
            if(sectionOfNumTwoIsHoveredOver) // if(mouseIsInreversedDigitBox)
            {
                // Red Text TEMPORARY HOVERED-OVER DRAWING (indicates a changed value if the hitbox is clicked)
                g2.setColor(Color.RED); // if(mouseIs_onScreen){textColor = red}

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .15f)); // Makes token(text) 15% opaque
                if(fullNumTwoDigitsList[reversedDigit] == DIGIT_ONE) // if(mouseIsInHitbox && hitbox==1){seethru text in red}
                {
                    // if(mouseIs_onScreen && mouseIsInSpecificHitbox && hitbox==1){draw translucent red "0"}
                    drawChangeStateToken(g2, "0",
                            (reversedDigit+numOfOverflowAndCarryDigits)*xLineSpacing + xMarginOffset,
                            2*yLineSpacing + yMarginOffset);
                    // (Graphics2D g2, String token, int upperLeftX, int upperLeftY)
                }
                else //if(fullNumTwoDigitsList[reversedDigit] != DIGIT_ONE)
                {
                    // if(mouseIs_onScreen && mouseIsInSpecificHitbox && hitbox==unassigned){draw translucent red "1"}
                    // if(mouseIs_onScreen && mouseIsInSpecificHitbox && hitbox==0){draw translucent red "1"}
                    drawChangeStateToken(g2, "1",
                            (reversedDigit+numOfOverflowAndCarryDigits)*xLineSpacing + xMarginOffset,
                            2*yLineSpacing + yMarginOffset);
                } // (Graphics2D g2, String token, int upperLeftX, int upperLeftY)
            }
        }
    }


    private void calculateAndDisplayAddedList(Graphics2D g2) {
    //PROGRAMMER ERROR: fullNumTwoDigitsList SHOULD INSTEAD BE SHORTEST LIST SO INDICES ARE NOT OVERRUN
    //Must start at LSB (Least Significant Bit) and work up to the MSB because of dependencies on lower-place-value bits, hence the flipped for()
        int reversedDigit = fullNumTwoDigitsList.length-1;
        for(int addedListDigit = reversedDigit+numOfOverflowAndCarryDigits;   addedListDigit>=0;   reversedDigit--, addedListDigit--)
        {
            // +1 is right, -1 is left. addedDigitsList should ALWAYS have at least one
            // extra digit than any #s being added.
            // Cases where sum==1: 0+1+(0*carry) 1+0+(0*carry) 1+1+(1*carry)
            // Cases where sum==0: 1+1+(0*carry) 0+1+(1*carry) 1+0+(1*carry)
            // Cases where carry==0: 0+1+(0*prevCarry) 1+0+(0*prevCarry)
            // Cases where carry==1: 1+1+(0*prevCarry) 1+1+(1*prevCarry)

            // Cases where added#_List=1: sum==1 == if( 0+1+(0*carry) || 1+0+(0*carry) || 1+1+(1*carry) )
            // Cases where added#_List=0: sum==0 == if( 1+1+(0*carry) || 0+1+(1*carry) || 1+0+(1*carry) )
            // Cases where carry==0: 0+1+(0*prevCarry) 1+0+(0*prevCarry)
            // Cases where carry==1: 1+1+(0*prevCarry) 1+1+(1*prevCarry)

            // addedList[i]=1 if( NumOneDigitsList[i]==0 && NumTwoDigitsList[i]==1 && carry==0
            //                 || NumOneDigitsList[i]==1 && NumTwoDigitsList[i]==0 && carry==0
            //                 || NumOneDigitsList[i]==1 && NumTwoDigitsList[i]==1 && carry==1 )
            // addedList[i]=0 if( NumOneDigitsList[i]==1 && NumTwoDigitsList[i]==1 && carry==0
            //              || NumOneDigitsList[i]==0 && NumTwoDigitsList[i]==1 && carry==1
            //              || NumOneDigitsList[i]==1 && NumTwoDigitsList[i]==0 && carry==1 )
            // carry=0 if( NumOneDigitsList[i]==0 && NumTwoDigitsList[i]==1 && prevCarry==0
            //          || NumOneDigitsList[i]==1 && NumTwoDigitsList[i]==0 && prevCarry==0 )
            // carry=1 if( NumOneDigitsList[i]==1 && NumTwoDigitsList[i]==1 && prevCarry==0
            //          || NumOneDigitsList[i]==1 && NumTwoDigitsList[i]==1 && prevCarry==1 )

            if(reversedDigit>=0)
            {
                boolean bothCurrBitsAre0 = fullNumOneDigitsList[reversedDigit] == 0 && fullNumTwoDigitsList[reversedDigit] == 0;
                boolean bothCurrBitsAre1 = fullNumOneDigitsList[reversedDigit] == 1 && fullNumTwoDigitsList[reversedDigit] == 1;
                boolean exactlyOneOfCurrBitsIs1 = fullNumOneDigitsList[reversedDigit] == 0 && fullNumTwoDigitsList[reversedDigit] == 1
                                            || fullNumOneDigitsList[reversedDigit] == 1 && fullNumTwoDigitsList[reversedDigit] == 0;
                //String debugStr = "AddedBitsList["+ addedListDigit +"]";
                if(exactlyOneOfCurrBitsIs1 && carry==0  ||
                ((bothCurrBitsAre1  || bothCurrBitsAre0) && carry==1 && /*so its carry doesn't cyclically affect itself*/addedListDigit!=addedDigitsList.length-1))
                    {addedDigitsList[addedListDigit] = 1;
                    //printl("exactlyOneOfCurrBitsIs1 && carry==0: "+(exactlyOneOfCurrBitsIs1 && carry==0));
                    //printl("bothCurrBitsAre1 && carry==1: "+(bothCurrBitsAre1 && carry==1));
                    }//printl(debugStr+"=1");}   //sum==...1
                if(bothCurrBitsAre1 && carry==0  ||  exactlyOneOfCurrBitsIs1 && carry==1)
                    {addedDigitsList[addedListDigit] = 0;
                    }//printl(debugStr+"=0");}   //sum==...0
                if(exactlyOneOfCurrBitsIs1 && carry == 0 || bothCurrBitsAre0)
                    {carry = 0;
                    }//printl(debugStr+"_carry = 0");}
                if(bothCurrBitsAre1 )//&& carry == 0  ||  bothCurrBitsAre1 && carry == 1
                    {carry = 1;
                    }//printl(debugStr+"_carry = 1");}
            }
            else    //Deal with the extra overflow and carry bits
            {
                if(addedListDigit==0/*carry bit index*/ && carry==1)
                    {addedDigitsList[addedListDigit] = 1;}
                else
                    {addedDigitsList[addedListDigit] = 0;}
            }

            // All comparing same place holder
            // ~ addedDigitsList[reversedDigit+1] = fullNumOneDigitsList[reversedDigit] ^ fullNumTwoDigitsList[reversedDigit]; //XOR

            // Added[] is left one place value of Num1 and Num2
            // ~ carry = (fullNumOneDigitsList[reversedDigit]==1 && fullNumTwoDigitsList[reversedDigit]==1); //AND. DON'T USE BITWISE BC 0&0=1

            // ~ if(carry && (fullNumOneDigitsList[reversedDigit-1] ^
            // fullNumTwoDigitsList[reversedDigit-1])==1)
            // ~ {
            // ~ addedDigitsList[reversedDigit+1]=0;
            // ~ addedDigitsList[reversedDigit] =1;
            // ~ addedDigitsList[reversedDigit-1]=1;
            // ~ }
            // ~ else if(carry)
            // ~ {
            // ~ addedDigitsList[reversedDigit] = 1;
            // ~ addedDigitsList[reversedDigit+1] = 0;
            // ~ }
            // ~ addedDigitsList[reversedDigit] = fullNumOneDigitsList[reversedDigit] &
            // fullNumTwoDigitsList[reversedDigit];
        }

        g2.setColor(Color.MAGENTA);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // Makes token 100% opaque
        for(int addedListDigit=0; addedListDigit<addedDigitsList.length; addedListDigit++)
        {
            drawChangeStateToken(g2, addedDigitsList[addedListDigit] == DIGIT_ONE ? "1" : "0",
                    addedListDigit*xLineSpacing + xMarginOffset,   3*yLineSpacing + yMarginOffset);
        }
    }

    // Overwrites the default paint() method that the frame?/panel? uses
    // PAINT DRAWS THE STATE
    public void paint(Graphics g) // The system calls this, so you don't need to call it in main.
    {
        System.out.println("Canvas has been repainted");

        // Since this is logic, it can't be made global :(
        // Setting up the addedNumList
        if(numOneHasMoreDigitsThanNumTwo)
            {addedDigitsList = new int[numOne_numOfBinDigitsPerBaseTenNumber + numOfOverflowAndCarryDigits];}
        else
            {addedDigitsList = new int[numTwo_numOfBinDigitsPerBaseTenNumber + numOfOverflowAndCarryDigits];}

        // Setting the greatestNumOfDigits based on which added# is greater
        if(numOneHasMoreDigitsThanNumTwo)
            {greatestNumOfDigits = numOne_numOfBinDigitsPerBaseTenNumber;}
        else{greatestNumOfDigits = numTwo_numOfBinDigitsPerBaseTenNumber;}

        // ~ reset();
        Graphics2D g2 = graphics2DSetup(g);

        drawBackground_AndNonFunctionalBoundingBoxes(c3, c2, g, g2);
        // (Color c3, Color c2, Graphics g, Graphics2D g2)
        // (bkgrnd color, Nonfunctionalhitbox color, graphics utility, advanced graphics utility)

        calculateAndDisplayAddedList(g2);
        drawNumOne(g2);
        drawNumTwo(g2);

        // ~ repaint(); //Updates it continuously. Calls paint when it gets time
    }

    // LISTENERS CHANGE THE STATE
    public void mouseEntered(MouseEvent Mouzers) {}
    public void mouseExited(MouseEvent Mouzers)  {}
    public void mouseReleased(MouseEvent Mouzers){}
    public void mouseDragged(MouseEvent Mouzers) {}
    public void mousePressed(MouseEvent Mouzers) {} // Only activated once mouse is pressed down AND released
    public void mouseClicked(MouseEvent Mouzers)
    {
        assignAllNums();
        repaint();// THAT'S WHY I COULDN'T SEE ANYTHINGGGGGGGGGGGGGGGGGGG
    }

    public void mouseMoved(MouseEvent Mouzers) {
        mouseX = Mouzers.getX();
        mouseY = Mouzers.getY();
        System.out.println("X:"+mouseX + ", Y:"+mouseY);
        repaint();
    }

    public void keyPressed(KeyEvent Mouzers)
    {
        if(Mouzers.getKeyCode() == 32/*space bar*/){reset();}
        repaint();
    }
    public void keyReleased(KeyEvent Mouzers) {}
    public void keyTyped(KeyEvent Mouzers)    {}
}

// if you have only an integer, arguments will interpret 0b prefix (before the number) as binary, 0x as hex, 0 as octal
// Integer.parseInt("121",3); # to be changed into base(radix)10, base # the # is already in
// Keyboards are by default sent to frame (focus: frame), not panel. Focus tells where input from keyboard, mouse, etc go
// void Graphics2D.drawImage(BufferedImage img, null, ?, ?). Must account for exceptions.
//DON'T USE IN REPAINT BC SO MUCH COMPUTER WORK FROM READING A FILE WHICH TAKES FOREVER.

/*
 * Interesting stuff ig?...
 * //FOR DIAGONALS?
 * sectionOfNumOneIsHoveredOver = mouseX>=i*xLineSpacing+xMarginOffset &&
 * mouseX<=(i+1)*xLineSpacing+xMarginOffset
 * && mouseY>=i*yLineSpacing+yMarginOffset &&
 * mouseY<=(i+1)*yLineSpacing+yMarginOffset;
 * //FOR DIAGONALS
 * sectionOfNumTwoIsHoveredOver =
 * mouseX>=reversedDigit*xLineSpacing+xMarginOffset &&
 * mouseX<=(reversedDigit+1)*xLineSpacing+xMarginOffset
 * && mouseY>=(reversedDigit+1)*yLineSpacing+yMarginOffset &&
 * mouseY<=(reversedDigit+2)*yLineSpacing+yMarginOffset;
 * // https://docs.oracle.com/javase/7/docs/api/java/awt/Font.html
 * g.fillOval((getWidth()-size)/2,(getHeight()-size)/2,size,size);
 * g2.fillOval(x+10,y+10,180,180);
 * super.paint(g2); //if this is called at the end of paint, it treats
 * everything before as a background
 * 
 * private void drawToken(Graphics2D g2, String token, int upperLeftX, int
 * upperLeftY)
 * {
 * Font font = new Font(/*"Arial""Ubuntu"
 *//*
    * "Serif", Font.PLAIN, 40);
    * g2.setFont(font);
    * //~ FontMetrics fontMetrics = g2.getFontMetrics();
    * //~ Rectangle2D bounds =
    * font.createGlyphVector(fontMetrics.getFontRenderContext(),
    * token).getVisualBounds();
    * //~ g2.drawString(token, upperLeftX, upperLeftY);
    * //~ g2.drawString(token, upperLeftX + fontMetrics.stringWidth(token)/2 -
    * (int)(bounds.getWidth()/2), upperLeftY + fontMetrics.getAscent()/2 +
    * fontMetrics.getHeight()/2);
    * for(int i=1;i<addedDigitsList.length;i++)
    * {
    * g2.drawString(token, (int)(i*xLineSpacing+xMarginOffset+.25*xLineSpacing),
    * yLineSpacing+yMarginOffset);
    * g2.drawString(token, (int)(i*xLineSpacing+xMarginOffset+.25*xLineSpacing),
    * 2*yLineSpacing+yMarginOffset);
    * } //+.25*xLineSpacing because token is drawn from the upper left, not the
    * middle
    * }
    * drawChangeStateToken(g2,
    * fullNumOneDigitsList[reversedDigit]==DIGIT_ONE?"1":"0",reversedDigit*
    * xLineSpacing+xMarginOffset,yMarginOffset);
    * 
    */