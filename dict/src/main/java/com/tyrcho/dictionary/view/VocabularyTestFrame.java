package com.tyrcho.dictionary.view;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.tyrcho.dictionary.DictionaryConstants;
import com.tyrcho.dictionary.Utils;
import com.tyrcho.dictionary.model.DictionaryEntry;
import com.tyrcho.dictionary.model.Question;
import com.tyrcho.dictionary.model.Session;
import com.tyrcho.dictionary.model.SessionCompleteEvent;
import com.tyrcho.dictionary.model.SessionCompleteListener;
import com.tyrcho.gui.component.console.CommandEvent;
import com.tyrcho.gui.component.console.CommandEventListener;
import com.tyrcho.gui.component.console.ConsolePanel;

@SuppressWarnings("serial")
public class VocabularyTestFrame extends JFrame {
    private ConsolePanel                  console;
    private List<SessionCompleteListener> listeners     = new LinkedList<SessionCompleteListener>();
    private Session                       session;
    private Iterator<Question>            iterator;
    private Question                      currentQuestion;
    private CommandEventListener          listener      = new CommandEventListener() {
                                                            public void commandPerformed(CommandEvent e) {
                                                                questionAnswered(e.getText().trim());
                                                            }
                                                        };
    private StringBuffer errors=new StringBuffer();                                                    

    public VocabularyTestFrame(String title, Session session) {
        super(title);
        this.session = session;
        console = new ConsolePanel();
        console.setFont(DictionaryConstants.FONT);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(console.getPanel(), BorderLayout.CENTER);
    }

    public String getErrors() {
        return errors.toString();
    }

    public void addSessionCompleteListener(SessionCompleteListener listener) {
        listeners.add(listener);
    }

    public void removeSessionCompleteListener(SessionCompleteListener listener) {
        listeners.remove(listener);
    }

    protected void fireSessionCompleteEvent(String score) {
        SessionCompleteEvent event = new SessionCompleteEvent(this, score);
        for (SessionCompleteListener listener : listeners) {
            listener.sessionComplete(event);
        }
    }

    public synchronized void runSession() {
        pack();
        setVisible(true);
        console.clear();
        console.requestFocus();
        iterator = session.iterator();
        console.addCommandEventListener(listener);
        nextQuestion();
    }

    private void questionAnswered(String answer) {
        currentQuestion.setInputTranslation(answer);
        DictionaryEntry dictionaryEntry = currentQuestion.dictionnaryEntry();
        if (currentQuestion.isAnswerValid()) {
            session.updateScore();
            dictionaryEntry.incrementGoodAnswers();
            dictionaryEntry.setRating(session.newRating(dictionaryEntry.getRating(), true));
//            int goodAnswers = dictionaryEntry.goodAnswers();
            String score=dictionaryEntry.displayRating();
            	//"("+goodAnswers+"/"+(goodAnswers+dictionaryEntry.getWrongAnswers())+")";
            console.println("Bravo "+score);
        } else {
        	DictionaryEntry possibleConfusion = session.getPossibleConfusion(answer.trim());
        	if(possibleConfusion!=null) {
				console.println(String.format("*** %s == %s (%s)***", answer,
						possibleConfusion.getFirstTranslation(),
						possibleConfusion.explaination()));
        		possibleConfusion.incrementWrongAnswers();
        		possibleConfusion.setRating(session.newRating(possibleConfusion.getRating(), false));

        	}
            int translations=dictionaryEntry.translations().size();
            dictionaryEntry.incrementWrongAnswers();
            dictionaryEntry.setRating(session.newRating(dictionaryEntry.getRating(), false));
//            int goodAnswers = dictionaryEntry.goodAnswers();
//            String score="("+goodAnswers+"/"+(goodAnswers+dictionaryEntry.getWrongAnswers())+")";
            String score=dictionaryEntry.displayRating();
            String message= (translations>1 ? "les traductions possible pour " : "la bonne traduction pour ")+currentQuestion.getWord()+" : "+ currentQuestion.getTranslation();
            errors.append(String.format("%s et non <%s> (%s)%n",message,answer,dictionaryEntry.explaination()));
            console.println("*** ERREUR *** "+score+", "+message);
            
        }
        String example=dictionaryEntry.explaination();
        if (example!=null && example.trim()!="") {
            console.println(example);
        }
        console.println();
        nextQuestion();
    }

    private void nextQuestion() {
        if (iterator.hasNext()) {
            currentQuestion = iterator.next();
            console.println(currentQuestion.toString());
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    console.removeCommandEventListener(listener);
                    fireSessionCompleteEvent(session.getScore());
                }
            });
        }
    }

   
}
