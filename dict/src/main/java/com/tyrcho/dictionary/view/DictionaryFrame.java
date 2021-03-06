package com.tyrcho.dictionary.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import com.tyrcho.dictionary.DictionaryConstants;
import com.tyrcho.dictionary.model.DictionaryEntry;
import com.tyrcho.dictionary.model.Session;
import com.tyrcho.dictionary.model.SessionCompleteEvent;
import com.tyrcho.dictionary.model.SessionCompleteListener;
import com.tyrcho.dictionary.model.SessionParameters;
import com.tyrcho.dictionary.model.TwoWayDictionary;
import com.tyrcho.dictionary.model.factory.DictionnaryFactory;
import com.tyrcho.dictionary.model.factory.DictionnaryFactoryException;
import com.tyrcho.dictionary.model.factory.XstreamDictionaryFactory;
import com.tyrcho.dictionary.util.ExcelExporter;
import com.tyrcho.gui.component.ErrorMessageDialog;
import com.tyrcho.gui.toolkit.RadioButtonGroup;

/**
 * @author ALEXIS
 */
public class DictionaryFrame extends JFrame {
  private static final String HELP_PAGE = "https://sites.google.com/site/micheldaviot/hobbies/apprentissage-du-chinois/logiciel";

  public static final String STRING_SEPARATOR = ",( )*";

  public static final String EXTENSION = "dict";

  public static final String propertiesFileName = System.getProperty("user.home")
      + "/dict.properties";

  private String firstLanguageName;

  private String secondLanguageName;

  private TwoWayDictionary dictionary;

  private DefaultListModel listModel = new DefaultListModel();

  private WordsListDisplay wordsList = new WordsDisplayAsList(listModel);

  private JButton newButton = new JButton("Nouveau");
  private JButton helpButton = new JButton("Aide");

  private JButton deleteButton = new JButton("Supprimer");

  private JTextField searchField = new JTextField(10);

  private RadioButtonGroup languageSelector;

  private boolean firstLanguageSelected;

  private EntryPanel entryPanel = new EntryPanel(this);

  private File currentFile;

  private boolean modified;

  private String lastSelectedWord;

  private Action saveAction = new AbstractAction("Enregistrer") {
    public void actionPerformed(ActionEvent e) {
      saveClicked();
    }
  };

  private Action exportAction = new AbstractAction("Exporter") {
    public void actionPerformed(ActionEvent e) {
      exportClicked();
    }
  };

  private Action trainingAction = new AbstractAction("Entrainement") {
    public void actionPerformed(ActionEvent e) {
      runTraining();
    }
  };

  private Action saveAsAction = new AbstractAction("Enregistrer sous") {
    public void actionPerformed(ActionEvent e) {
      saveAsClicked();
    }
  };

  private Action loadAction = new AbstractAction("Charger") {
    public void actionPerformed(ActionEvent e) {
      loadClicked();
    }
  };

  private Action helpAction = new AbstractAction("Aide") {
    public void actionPerformed(ActionEvent e) {
      showHelp();
    }
  };

  private Action importAction = new AbstractAction("Importer") {
    public void actionPerformed(ActionEvent e) {
      importClicked();
    }
  };

  private Action newDictionaryAction = new AbstractAction("Nouveau dictionnaire") {
    public void actionPerformed(ActionEvent e) {
      newDictionaryClicked();
    }
  };

  private JLabel statusBar = new JLabel();

  private DictionnaryFactory factory = new XstreamDictionaryFactory();

  private String ignoredChars;

  public DictionaryFrame(String firstLanguageName, String secondLanguageName) {
    dictionary = new TwoWayDictionary(firstLanguageName, secondLanguageName);
    this.firstLanguageName = firstLanguageName;
    this.secondLanguageName = secondLanguageName;
    updateTitle();
    setupFrame();
  }

  private void runTraining() {
    SessionParameters parameters = SessionParametersPanel.showSessionParametersDialog(this,
        "Choisir la configuration de l'exercice", dictionary);
    if (parameters != null) {
      parameters.setIgnoredChars(ignoredChars);
      Session session = new Session(parameters);
      runSession(session);
      setModified(true);
    }
  }

  private Boolean popupChooseLanguage() {
    String[] choices = new String[] { firstLanguageName, secondLanguageName };
    Object choice = JOptionPane.showInputDialog(this, "Choisir le langage de tri", "",
        JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
    boolean firstLanguage;
    if (choice == null) {
      return null;
    } else {
      firstLanguage = choice == choices[0];
    }
    return firstLanguage;
  }

  private void runSession(final Session session) {
    final VocabularyTestFrame frame = new VocabularyTestFrame("Entrainement", session);
    // final JFrame mainFrame=this;
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.addSessionCompleteListener(new SessionCompleteListener() {
      public void sessionComplete(final SessionCompleteEvent e) {
        DictionaryFrame.this.sessionComplete(e.getScore(), session, frame);
      }
    });
    frame.runSession();
  }

  private void sessionComplete(final String score, final Session session,
      final VocabularyTestFrame frame) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        // String otherChangeLanguage= "Autre exercice
        // ("+(session.isFirstLanguage()?secondLanguageName:firstLanguageName)+")";
        String[] options = { "Recommencer (memes mots)", "Autre exercice", "Changer de langue ",
            "Retour", "Meme type d'exercice" };
        String message = "Votre score est de " + score + ".\n " + frame.getErrors()
            + "\nEt maintenant ?";
        int choice = JOptionPane.showOptionDialog(frame, message, "Session terminee",
            JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[4]);
        frame.dispose();
        switch (choice) {
        case 3:
        default:
          break;
        case 0:
          session.resetScore();
          runSession(session);
          break;
        case 1:
          runTraining();
          break;
        case 2:
          session.switchLanguage();
          session.resetScore();
          runSession(session);
          break;
        case 4:
          session.resetQuestions();
          session.resetScore();
          runSession(session);
          break;
        }
      }
    });

  }

  private void updateTitle() {
    setTitle("Dictionnaire " + firstLanguageName + "-" + secondLanguageName);
  }

  private void loadClicked() {
    if (!isSaved("Attention aux donnees en cours", "Sauver les donnees en cours ?"))
      return;
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(filter);
    fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this)) {
      File file = fileChooser.getSelectedFile();
      load(file);
    }
  }

  private void importClicked() {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(filter);
    fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this)) {
      File file = fileChooser.getSelectedFile();
      importFile(file);
    }
  }

  private void updateFrame(File file, TwoWayDictionary dictionary) {
    DictionaryFrame frame = new DictionaryFrame(dictionary.getFirstLanguage(),
        dictionary.getSecondLanguage());
    frame.dictionary = dictionary;
    frame.ignoredChars = ignoredChars;
    frame.setCurrentFile(file);
    frame.updateList();
    frame.pack();
    frame.setBounds(getBounds());
    frame.updateStatus();
    frame.setVisible(true);
    dispose();
  }

  private void load(File file) {
    try {
      factory.setFileName(file.getAbsolutePath());
      dictionary = factory.load();
      updateFrame(file, dictionary);
    } catch (RuntimeException e) {
      new ErrorMessageDialog(this, "Fichier non valide", "Ce fichier n'a pas pu etre lu "
          + file.getAbsolutePath(), e).setVisible(true);
      updateFrame(file, new TwoWayDictionary("", ""));
    } catch (DictionnaryFactoryException e) {
      new ErrorMessageDialog(this, "Fichier non valide", "Ce fichier n'a pas pu etre lu "
          + file.getAbsolutePath(), e).setVisible(true);
      updateFrame(file, new TwoWayDictionary("", ""));
    }
  }

  private void importFile(File file) {
    try {
      factory.setFileName(file.getAbsolutePath());
      TwoWayDictionary imported = factory.load();
      modified = true;
      dictionary.addAll(imported);
      updateFrame(currentFile, dictionary);
    } catch (RuntimeException e) {
      new ErrorMessageDialog(this, "Fichier non valide", "Ce fichier n'a pas pu etre lu "
          + file.getAbsolutePath(), e).setVisible(true);
    } catch (DictionnaryFactoryException e) {
      new ErrorMessageDialog(this, "Fichier non valide", "Ce fichier n'a pas pu etre lu "
          + file.getAbsolutePath(), e).setVisible(true);
    }
  }

  private void exportClicked() {
    Boolean firstLanguage = popupChooseLanguage();
    if (firstLanguage != null) {
      final JFileChooser fileChooser = new JFileChooser();
      if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(this)) {
        File file = fileChooser.getSelectedFile();
        if (!(file.getName().indexOf('.') > 0)) {
          file = new File(file.getAbsolutePath() + ".xls");
        }
        if (!file.exists()
            || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Ecraser le fichier "
                + file.getAbsolutePath(), "Ecraser ?", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE)) {
          export(file, firstLanguage);
          final File f = file;
          JLabel label = new JLabel("Le fichier " + file.getAbsolutePath() + " a ete enregistre.");
          JButton button = new JButton("Open file");
          button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              try {
                Desktop.getDesktop().open(f);
              } catch (IOException e1) {
                e1.printStackTrace();
              }
            }
          });
          JPanel panel = new JPanel(new BorderLayout());
          panel.add(label, BorderLayout.CENTER);
          panel.add(button, BorderLayout.SOUTH);
          JOptionPane.showMessageDialog(this, panel);
        }
      }
    }
  }

  private void export(File file, boolean firstLanguage) {
    try {
      List<String> entries = dictionary.getSortedEntries(firstLanguage);
      String[][] data = new String[entries.size()][3];
      int i = 0;
      for (String word : entries) {
        String[] line = data[i++];
        line[0] = word;
        DictionaryEntry entry = dictionary.getEntry(word, firstLanguage);
        Collection<String> translations = entry.translations();
        StringBuffer buffer = new StringBuffer();
        for (Iterator<String> t = translations.iterator(); t.hasNext();) {
          buffer.append(t.next());
          if (t.hasNext()) {
            buffer.append(", ");
          }
        }
        line[1] = buffer.toString();
        line[2] = entry.explaination();
      }
      String lang1 = firstLanguage ? firstLanguageName : secondLanguageName;
      String lang2 = firstLanguage ? secondLanguageName : firstLanguageName;
      ExcelExporter.export(lang1, lang2, data, file);
    } catch (IOException e) {
      showSaveError(file, e);
    }
  }

  private boolean saveAsClicked() {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(filter);
    if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(this)) {
      File file = fileChooser.getSelectedFile();
      if (!(file.getName().indexOf('.') > 0)) {
        file = new File(file.getAbsolutePath() + "." + EXTENSION);
      }
      if (!file.exists()
          || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Ecraser le fichier "
              + file.getAbsolutePath(), "Ecraser ?", JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE)) {
        return save(file);
      }
    }
    return false;
  }

  private boolean saveClicked() {
    return save(currentFile);
  }

  private boolean save(File file) {
    try {
      factory.setFileName(file.getAbsolutePath());
      factory.save(dictionary);
      saveAction.setEnabled(true);
      setModified(false);
      updateStatus();
      JOptionPane.showMessageDialog(this, "Le fichier " + file.getAbsolutePath()
          + " a ete enregistre.");
      return true;
    } catch (DictionnaryFactoryException e) {
      showSaveError(file, e);
      return false;
    }
  }

  private void showSaveError(File file, Exception e) {
    new ErrorMessageDialog(this, "Impossible d'enregistrer", "Impossible d'ecrire dans le fichier "
        + file.getAbsolutePath(), e).setVisible(true);
  }

  private void updateStatus() {
    statusBar.setText((currentFile == null ? "" : currentFile.getName()) + " "
        + (isModified() ? "modifie" : ""));
  }

  private boolean isSaved(String title, String message) {
    if (!modified)
      return true;
    int response = JOptionPane.showConfirmDialog(this, message, title,
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    switch (response) {
    case JOptionPane.YES_OPTION:
      if (currentFile == null) {
        return saveAsClicked();
      } else {
        return saveClicked();
      }

    case JOptionPane.NO_OPTION:
      return true;

    case JOptionPane.CANCEL_OPTION:
    default:
      return false;
    }
  }

  public void addEntry(String previousWord, String word, String translations, String explanation) {
    String[] translationArray = translations.split(STRING_SEPARATOR);
    dictionary.removeWord(previousWord, firstLanguageSelected);
    dictionary.removeWord(word, firstLanguageSelected);
    dictionary.addExplaination(word, explanation, firstLanguageSelected);
    for (String translation : translationArray) {
      if (firstLanguageSelected) {
        dictionary.addTranslation(word, translation);
      } else {
        dictionary.addTranslation(translation, word);
      }
      dictionary.addExplaination(translation, explanation, !firstLanguageSelected);
    }
    if (!listModel.contains(word))
      updateList();
  }

  private void setupFrame() {
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    contentPane.add(splitPane, BorderLayout.CENTER);
    contentPane.add(statusBar, BorderLayout.SOUTH);
    splitPane.setLeftComponent(buildLeftPanel());
    splitPane.setRightComponent(entryPanel);
    entryPanel.setEnabled(false);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        updateLanguageSelected();
      }
    });
    setupListeners();
    languageSelector.setCurrentValue(secondLanguageName);
    setupMenu();
  }

  private void setupMenu() {
    JMenuBar menuBar = new JMenuBar();
    saveAction.setEnabled(false);
    menuBar.add(new JButton(newDictionaryAction));
    menuBar.add(Box.createGlue());
    menuBar.add(new JButton(helpAction));
    menuBar.add(new JButton(loadAction));
    menuBar.add(new JButton(saveAction));
    menuBar.add(new JButton(saveAsAction));
    menuBar.add(new JButton(importAction));
    menuBar.add(Box.createGlue());
    menuBar.add(new JButton(trainingAction));
    menuBar.add(Box.createGlue());
    menuBar.add(new JButton(exportAction));
    setJMenuBar(menuBar);
  }

  private void setupListeners() {
    searchField.getDocument().addDocumentListener(new DocumentListener() {
      private Runnable updateListRunner = new Runnable() {
        public void run() {
          updateList();
        }
      };

      public void insertUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(updateListRunner);
      }

      public void removeUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(updateListRunner);
      }

      public void changedUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(updateListRunner);
      }
    });
    wordsList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting())
          listSelectionChanged(getSelectedWord());
      }
    });
    newButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        newButtonClicked();
      }
    });
    helpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showHelp();
      }
    });
    deleteButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteButtonClicked();
      }
    });
    languageSelector.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            updateLanguageSelected();
          }
        });
      }
    });
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        if (isSaved("Confirmer la sortie", "Sauver avant de quitter ?"))
          dispose();
      }
    });
  }

  private void listSelectionChanged(String selectedWord) {
    if (selectedWord != null && selectedWord.length() > 0) {
      if (entryPanel.isModified() && lastSelectedWord != null
          && !selectedWord.equals(lastSelectedWord)) {
        int response = JOptionPane.showConfirmDialog(this, "Les donnees ont ete modifiees",
            "Conserver les modifications ?", JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        switch (response) {
        case JOptionPane.YES_OPTION:
          entryPanel.okButtonClicked();
          break;

        case JOptionPane.NO_OPTION:
          break;

        case JOptionPane.CANCEL_OPTION:
        default:
          wordsList.setSelectedValue(selectedWord, false);
          return;
        }
      }
      DictionaryEntry entry = firstLanguageSelected ? dictionary
          .getFirstLanguageEntry(selectedWord) : dictionary.getSecondLanguageEntry(selectedWord);
      entryPanel.setDictionaryEntry(selectedWord, entry);
      entryPanel.setEnabled(true);
    } else {
      entryPanel.clear();
      entryPanel.setEnabled(false);
    }
    lastSelectedWord = selectedWord;
  }

  private Component buildLeftPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    Box topBox = Box.createHorizontalBox();
    topBox.add(newButton);
    topBox.add(Box.createHorizontalStrut(10));
    topBox.add(deleteButton);
    topBox.add(Box.createHorizontalStrut(10));
    topBox.add(new JLabel("Rechercher "));
    topBox.add(searchField);
    panel.add(topBox, BorderLayout.NORTH);
    wordsList.setFont(DictionaryConstants.FONT);
    panel.add(new JScrollPane((Component) wordsList), BorderLayout.CENTER);
    Map<String, String> languages = new HashMap<String, String>();
    languages.put(firstLanguageName, firstLanguageName);
    languages.put(secondLanguageName, secondLanguageName);
    languageSelector = new RadioButtonGroup(RadioButtonGroup.HORIZONTAL, languages);
    panel.add(languageSelector, BorderLayout.SOUTH);
    return panel;
  }

  protected void deleteButtonClicked() {
    String selectedWord = getSelectedWord();
    if (firstLanguageSelected) {
      dictionary.removeFirstLanguageWord(selectedWord);
    } else {
      dictionary.removeSecondLanguageWord(selectedWord);
    }
    listModel.removeElement(selectedWord);
    setModified(true);
  }

  protected void newButtonClicked() {
    entryPanel.clear();
    entryPanel.setEnabled(true);
    entryPanel.requestFocus();
  }

  private void updateLanguageSelected() {
    String currentValue = (String) languageSelector.getCurrentValue();
    boolean languageSelectionChanged = firstLanguageSelected != firstLanguageName
        .equals(currentValue);
    if (languageSelectionChanged) {
      firstLanguageSelected = !firstLanguageSelected;
      updateList();
    }
    entryPanel.setLanguages(firstLanguageSelected ? firstLanguageName : secondLanguageName,
        firstLanguageSelected ? secondLanguageName : firstLanguageName);
  }

  private void updateList() {
    List<String> listData = dictionary.getSortedEntries(firstLanguageSelected);
    String searchFilter = searchField.getText().trim();
    if (searchFilter.length() > 0) {
      searchFilter = ".*" + searchFilter + ".*";
      for (Iterator<String> i = listData.iterator(); i.hasNext();) {
        String word = i.next();
        if (!word.matches(searchFilter))
          i.remove();
      }
    }
    listModel.removeAllElements();
    for (String word : listData) {
      listModel.addElement(word);
    }
    wordsList.setSelectedIndex(0);
  }

  private String getSelectedWord() {
    return (String) wordsList.getSelectedValue();
  }

  public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, UnsupportedLookAndFeelException {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    DictionaryFrame frame = new DictionaryFrame("Français", "Español");
    // frame.addEntry("hola", "salut, bonjour", "Hola seeor");
    frame.pack();
    try {
      frame.load(new File(readPropertiesFile()));
    } catch (IOException e) {
      System.out.println("Premier demarrage, le fichier " + propertiesFileName
          + " n'existe pas encore");
      frame.setVisible(true);
    }
  }

  /**
   * @return Returns the modified.
   * @uml.property name="modified"
   */
  public boolean isModified() {
    return modified;
  }

  /**
   * @param modified
   *          The modified to set.
   * @uml.property name="modified"
   */
  public void setModified(boolean modified) {
    this.modified = modified;
    updateStatus();
  }

  private static FileFilter filter = new FileFilter() {

    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().endsWith(EXTENSION);
    }

    @Override
    public String getDescription() {
      return "*." + EXTENSION;
    }

  };

  public File getCurrentFile() {
    return currentFile;
  }

  public static void updatePropertiesFile(File currentFile) {
    Properties properties = new Properties();
    properties.put("currentFile", currentFile.getAbsolutePath());
    try {
      FileOutputStream os = new FileOutputStream(propertiesFileName);
      properties.store(os, "dictionary");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String readPropertiesFile() throws IOException {
    Properties properties = new Properties();
    FileInputStream os = new FileInputStream(propertiesFileName);
    properties.load(os);
    return properties.getProperty("currentFile");
  }

  public void setCurrentFile(File currentFile) {
    this.currentFile = currentFile;
    if (currentFile == null) {
      saveAction.setEnabled(false);
    } else {
      saveAction.setEnabled(true);
      updatePropertiesFile(currentFile);
    }
  }

  private String selectLanguage(String label, String def) {
    String[] isoLanguages = Locale.getISOLanguages();
    Language[] locales = new Language[isoLanguages.length];
    for (int i = 0; i < isoLanguages.length; i++) {
      locales[i] = new Language(isoLanguages[i]);
    }
    Arrays.sort(locales);
    Language selected = (Language) JOptionPane.showInputDialog(this, label, null,
        JOptionPane.INFORMATION_MESSAGE, null, locales, Locale.forLanguageTag(def));
    return selected.code;
  }

  static class Language implements Comparable<Language> {
    private String code;

    public Language(String code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return Locale.forLanguageTag(code).getDisplayLanguage();
    }

    public int compareTo(Language o) {
      return toString().compareTo(o.toString());
    }
  }

  public void newDictionaryClicked() {
    if (!isSaved("Attention aux données en cours", "Sauver les données en cours ?"))
      return;
    String firstLanguage = selectLanguage("Première langue ?", "fr");
    String secondLanguage = selectLanguage("Deuxième langue ?", "en");
    dictionary = new TwoWayDictionary(firstLanguage, secondLanguage);
    updateFrame(null, dictionary);
  }

  private void showHelp() {
    try {
      Desktop.getDesktop().browse(new URI(HELP_PAGE));
    } catch (Exception e) {
      new ErrorMessageDialog(this, "Impossible d'ouvrir l'aide", "Impossible d'ouvrir l'adresse "
          + HELP_PAGE, e).setVisible(true);
    }
  }
}
