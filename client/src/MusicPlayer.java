import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.lang.Math;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;
import com.mysql.jdbc.PreparedStatement;


public class MusicPlayer extends JPanel{
	private Dimension dim;
	private int song;
	private JLabel album;
	private ImageIcon albumImage;
	private JLabel artist;
	private JLabel rating;
	private JButton backButton;
	private JButton playButton;
	private JButton forwardButton;
	private JButton pauseButton;
	private JTextArea lyrics;
	private String songName;
	private MusicModel musicObject;
	private static Thread myThread;
	private JButton rateButton;
	private JButton commentButton;
	private JButton favoriteButton;
	
	private JPanel tabPanelMain;
	private ArrayList<JButton> allButtons;
	private ArrayList<MusicModel> allSongs;
	//keeps track of the current song in the allSongs arraylist, NOT the song id in SQL
	private int currentSong;

	private JPanel favoritePanel;
	private JPanel commentPanel;
	private JPanel comments;
	private JTextField comment;
	private JButton enter;
	private JScrollPane jspComments;
	private JPanel ratePanel;
	
	private int myRating;
	JButton oneStar;
	JButton twoStar;
	JButton threeStar;
	JButton fourStar;
	JButton fiveStar;
	
	ArrayList<JButton> ratingButtons;
	private ImageIcon emptyStar;
	private ImageIcon fullStar;
	private int currentPanel;
	private JButton favoriteLabel;
	private Icon emptyHeart;
	private ImageIcon fullHeart;
	private JLabel listens;
	private JPanel ratingPanel;
	JPanel mainPanel;
	private Boolean isGuest;
	private boolean beingPlayed = false;
	private boolean getComments = false;

	public MusicPlayer(Dimension d, ArrayList<JButton> buttons, Boolean b, ArrayList<MusicModel> songs, int currentSong)
	{
		this.currentSong = currentSong;
		this.allButtons = buttons;
		dim = d;
		isGuest = b;
		this.allSongs = songs;
		emptyStar = new ImageIcon("data/starOutline.png");
		fullStar = new ImageIcon("data/starFullBlack.png");
		musicObject = allSongs.get(currentSong);
		songName = musicObject.getSongName();
		initializeComponents();
		createUserGUI();
		setEventHandlers();
		setVisible(true);
		repaint();

	}
	
	public static void stopThread()
	{
		if (myThread != null)
			myThread.suspend();
	}	
	public static void resumeThread()
	{
		if (myThread != null)
			myThread.resume();
	}

	private void initializeComponents(){
		this.setSize(dim);
		currentPanel = 0;
		album = new JLabel("");
		album.setPreferredSize(new Dimension(3*dim.width/4, 3*dim.width/4));
		favoritePanel = new JPanel();
		commentPanel = new JPanel();
		
		ratePanel = new JPanel();
		comments = new JPanel();
		comments.setBackground(Color.BLACK);
		comment = new JTextField("comment");
		enter = new JButton("Enter");
		jspComments = new JScrollPane(comments, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		ratingButtons = new ArrayList<JButton>();
		setPreferredSize(new Dimension(dim.width, dim.height));
		
		try {
            URL imageurl = new URL(musicObject.getAlbumPath());
            BufferedImage img = ImageIO.read(imageurl);
            ImageIcon icon = new ImageIcon(img);
            Image ResizedImage = icon.getImage().getScaledInstance(3*dim.width/4, 3*dim.width/4, Image.SCALE_SMOOTH);
            album.setIcon(new ImageIcon(ResizedImage));
         } catch (IOException e) {
            e.printStackTrace();
         }
		
		
		artist = new JLabel(musicObject.getSongName() + " "+musicObject.getArtistName());
		artist.setPreferredSize(new Dimension(dim.width-10, dim.height/26));
		ratingPanel = new JPanel();
		ratingPanel.setPreferredSize(new Dimension(dim.width-10, dim.height/20));
		ratingPanel.setBackground(FirstPageGUI.white);
		rating = new JLabel("Overall Rating: ");
		ratingPanel.add(rating);
		listens = new JLabel("# of listens");
		listens.setPreferredSize(new Dimension(dim.width-10, dim.height/26));
		listens.setFont(FirstPageGUI.smallFont);
		listens.setForeground(FirstPageGUI.darkGrey);
		listens.setHorizontalAlignment(SwingConstants.CENTER);

		backButton = new JButton();
		
		playButton = new JButton();
		forwardButton = new JButton();
		pauseButton = new JButton();

		rateButton = new JButton();
		commentButton = new JButton();
		favoriteButton = new JButton();
	}
	
	private void createUserGUI()
	{
		setBackground(FirstPageGUI.white);
		JPanel buttonPanel = new JPanel();
		//buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, dim.width/50, dim.height));
		buttonPanel.setPreferredSize(new Dimension(dim.width, 6*dim.height/93));
		buttonPanel.setBackground(FirstPageGUI.green);
		
		commentButton.setOpaque(false);
		commentButton.setContentAreaFilled(false);
		commentButton.setBorderPainted(false);
		commentButton.setIcon(new ImageIcon("data/commentsSmall.png"));
		commentButton.setPreferredSize(new Dimension(dim.width/5, 5*dim.height/93));
		
		rateButton.setOpaque(false);
		rateButton.setContentAreaFilled(false);
		rateButton.setBorderPainted(false);
		rateButton.setIcon(new ImageIcon("data/ratingSmall.png"));
		rateButton.setPreferredSize(new Dimension(dim.width/5, 5*dim.height/93));
		
		favoriteButton.setOpaque(false);
		favoriteButton.setContentAreaFilled(false);
		favoriteButton.setBorderPainted(false);
		favoriteButton.setIcon(new ImageIcon("data/favorite_emptySmall.png"));
		favoriteButton.setPreferredSize(new Dimension(dim.width/5, 5*dim.height/93));
		
		buttonPanel.add(commentButton);
		if (!isGuest)
		{
			buttonPanel.add(favoriteButton);
		}
		buttonPanel.add(rateButton);
		
		JPanel bottomPanel = new JPanel();
		//bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, dim.width/50, dim.height));
		bottomPanel.setPreferredSize(new Dimension(dim.width, 6*dim.height/93));
		bottomPanel.setBackground(FirstPageGUI.green);
	
		backButton.setOpaque(false);
		backButton.setContentAreaFilled(false);
		backButton.setBorderPainted(false);
		backButton.setIcon(new ImageIcon("data/ReverseButtonSmall.png"));
		backButton.setPreferredSize(new Dimension(dim.width/5, 5*dim.height/93));
		
		playButton.setOpaque(false);
		playButton.setContentAreaFilled(false);
		playButton.setBorderPainted(false);
		playButton.setIcon(new ImageIcon("data/playButtonSmall.png"));
		playButton.setPreferredSize(new Dimension(dim.width/5, 5*dim.height/93));
		
		pauseButton.setOpaque(false);
		pauseButton.setContentAreaFilled(false);
		pauseButton.setBorderPainted(false);
		pauseButton.setIcon(new ImageIcon("data/pauseButtonSmall.png"));
		pauseButton.setPreferredSize(new Dimension(dim.width/5, 5*dim.height/93));
		
		forwardButton.setOpaque(false);
		forwardButton.setContentAreaFilled(false);
		forwardButton.setBorderPainted(false);
		forwardButton.setIcon(new ImageIcon("data/forwardButtonSmall.png"));
		forwardButton.setPreferredSize(new Dimension(dim.width/5, 5*dim.height/93));
		
		bottomPanel.add(backButton);
		bottomPanel.add(playButton);
		bottomPanel.add(pauseButton);
		bottomPanel.add(forwardButton);
		
		mainPanel = new JPanel();
		mainPanel.setBackground(FirstPageGUI.white);
		mainPanel.setPreferredSize(new Dimension(dim.width, 102*dim.height/186));
		mainPanel.add(album);
		mainPanel.add(artist);
		artist.setBackground(FirstPageGUI.white);
		rating.setBackground(FirstPageGUI.white);
		artist.setForeground(FirstPageGUI.darkGrey);
		rating.setForeground(FirstPageGUI.darkGrey);
		artist.setFont(FirstPageGUI.smallFont);
		rating.setFont(FirstPageGUI.smallFont);
		rating.setHorizontalAlignment(SwingConstants.CENTER);
		artist.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(ratingPanel);
		mainPanel.add(listens);
		mainPanel.add(bottomPanel);
		
		favoriteLabel = new JButton();
		favoritePanel = new JPanel();
		favoritePanel.setPreferredSize(new Dimension(dim.width, 15*dim.height/93));
		favoriteLabel.setPreferredSize(new Dimension(dim.width, 15*dim.height/93));
		favoritePanel.add(favoriteLabel);
		
		emptyHeart = new ImageIcon("data/heartOutline.png");
		fullHeart = new ImageIcon("data/fullHeartBlack.png");
		favoriteLabel.setOpaque(false);
		favoriteLabel.setContentAreaFilled(false);
		favoriteLabel.setBorderPainted(false);
		//favoriteLabel.setIcon(emptyHeart);
		
		//fiveStar.setPreferredSize(new Dimension(dim.width/6, dim.height/13));
		commentPanel = new JPanel();
		commentPanel.setPreferredSize(new Dimension(dim.width, 46*dim.height/186));
		ratePanel.setPreferredSize(new Dimension(dim.width,48*dim.height/186));
		comments.setPreferredSize(new Dimension(dim.width, dim.height));
		comment.setPreferredSize(new Dimension(3*dim.width/5, 6*dim.height/186));
		jspComments.setPreferredSize(new Dimension(dim.width, 33*dim.height/186));
		//comments.setBackground(FirstPageGUI.darkGrey);
		//jspComments.setBackground(FirstPageGUI.darkGrey);
		//commentPanel.setBackground(FirstPageGUI.darkGrey);
		favoritePanel.setBackground(FirstPageGUI.white);
		commentPanel.setBackground(FirstPageGUI.white);
		comments.setBackground(FirstPageGUI.white);
		
		ratePanel.setBackground(FirstPageGUI.white);
		enter.setPreferredSize(new Dimension(dim.width/5, 3*dim.height/93));
		
		enter.setBorder(new RoundedBorder());
		enter.setBackground(FirstPageGUI.darkGrey);
		enter.setForeground(FirstPageGUI.white);
		enter.setFont(FirstPageGUI.smallFont);
		enter.setOpaque(true);
		comment.setBorder(new RoundedBorder());
		comment.setBackground(FirstPageGUI.white);
		comment.setForeground(FirstPageGUI.darkGrey);
		comment.setFont(FirstPageGUI.smallFont);
		oneStar = new JButton();
		twoStar = new JButton();
		threeStar = new JButton();
		fourStar = new JButton();
		fiveStar = new JButton();
		
		oneStar.setOpaque(false);
		oneStar.setContentAreaFilled(false);
		oneStar.setBorderPainted(false);
		oneStar.setIcon(emptyStar);
		oneStar.setPreferredSize(new Dimension(dim.width/6, dim.height/13));
		
		fiveStar.setOpaque(false);
		fiveStar.setContentAreaFilled(false);
		fiveStar.setBorderPainted(false);
		fiveStar.setIcon(emptyStar);
		fiveStar.setPreferredSize(new Dimension(dim.width/6, dim.height/13));
		
		twoStar.setOpaque(false);
		twoStar.setContentAreaFilled(false);
		twoStar.setBorderPainted(false);
		twoStar.setIcon(emptyStar);
		twoStar.setPreferredSize(new Dimension(dim.width/6, dim.height/13));
		
		threeStar.setOpaque(false);
		threeStar.setContentAreaFilled(false);
		threeStar.setBorderPainted(false);
		threeStar.setIcon(emptyStar);
		threeStar.setPreferredSize(new Dimension(dim.width/6, dim.height/13));
		
		fourStar.setOpaque(false);
		fourStar.setContentAreaFilled(false);
		fourStar.setBorderPainted(false);
		fourStar.setIcon(emptyStar);
		fourStar.setPreferredSize(new Dimension(dim.width/6, dim.height/13));
		
		ratingButtons.add(oneStar);
		ratingButtons.add(twoStar);
		ratingButtons.add(threeStar);
		ratingButtons.add(fourStar);
		ratingButtons.add(fiveStar);
		
		for (int i = 0; i <5; i++)
		{
			ratePanel.add(ratingButtons.get(i));
		}
		
		JPanel other = new JPanel();
		other.setPreferredSize(new Dimension(dim.width, 14*dim.height/186));
		other.add(enter, BorderLayout.WEST);
		other.add(comment, BorderLayout.EAST);
		commentPanel.add(jspComments, BorderLayout.CENTER);
		if (!isGuest)
		{
			commentPanel.add(other, BorderLayout.SOUTH);
		}

		JPanel tabPanel = new JPanel();
		
		
		tabPanel.setPreferredSize(new Dimension(dim.width, 66*dim.height/186));
		tabPanel.setBackground(FirstPageGUI.white);
		tabPanelMain = new JPanel();
		tabPanelMain.setPreferredSize(new Dimension(dim.width, 46*dim.height/186));
		//tabPanelMain.setBackground(Fir);
		//tabPanel.setBackground(FirstPageGUI.darkGrey);
		//tabPanelMain.setBackground(FirstPageGUI.darkGrey);
		tabPanelMain.add(commentPanel, BorderLayout.CENTER);
		tabPanelMain.setBackground(FirstPageGUI.white);
		tabPanel.add(tabPanelMain);
		tabPanel.add(buttonPanel);
		resetStuff();
		//setBackground(FirstPageGUI.green);
		//mainPanel.setBackground(FirstPageGUI.darkGrey);
		add(mainPanel, BorderLayout.NORTH);
		add(tabPanel, BorderLayout.SOUTH);
	}
	private void setEventHandlers(){
		comment.addFocusListener(new FocusListener()
		{

			@Override
			public void focusGained(FocusEvent e)
			{
				if(comment.getText().equals("comment"))
				{
					//editFirstName.setEchoChar(('*'));
					comment.setText("");
				}
				comment.setForeground(FirstPageGUI.darkGrey);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				if(comment.getText().equals(""))
				{
					comment.setText("comment");
					//editFirstName.setEchoChar((char)0);
					comment.setForeground(FirstPageGUI.lightGrey);
				}
				
			}
		});
		
		comment.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e){}
			public void keyReleased(KeyEvent e){}
			
			@Override
			public void keyTyped(KeyEvent e)
			{
				if(e.getKeyChar() == KeyEvent.VK_ENTER)
				{
					try
					{
						PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO comments_table (user_id,song_id,comment)" + "VALUES (?, ?, ?)");
						ps.setInt(1, LoggedInDriverGUI.userID);
						ps.setInt(2, musicObject.getMusicID());
						ps.setString(3, comment.getText());
						ps.executeUpdate();
						ps.close();	
					}
					catch (Exception E)
					{
						
					}
					JPanel outer = new JPanel();
					outer.setLayout(new FlowLayout(FlowLayout.LEFT));
					outer.setPreferredSize(new Dimension(2*dim.width/2, 9*dim.height/200));
					JLabel name = new JLabel();
					name.setPreferredSize(new Dimension(8*dim.width/24, 9*dim.height/200));
					name.setText("@"+LoggedInDriverGUI.username+":");
					JLabel commentLabel = new JLabel();
					commentLabel.setPreferredSize(new Dimension(4*dim.width/12, 9*dim.height/200));
					commentLabel.setText(comment.getText());
					outer.add(name);
					outer.add(commentLabel);
					comment.setText("");
					comments.add(outer);
					comments.revalidate();
					comments.repaint();
                }       
			}
		});
		favoriteLabel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{

				try
				{
				//ConnectionClass.conn = DriverManager.getConnection("jdbc:mysql://104.236.176.180/cs201", "cs201", "manishhostage");
				
				Statement st = ConnectionClass.conn.createStatement();
				//PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("SELECT song_id FROM favorite_songs WHERE user_id = " + Integer.toString(LoggedInDriverGUI.userID));
				String queryCheck = "SELECT song_id FROM favorite_songs WHERE user_id = " + Integer.toString(LoggedInDriverGUI.userID) +" AND song_id = " + Integer.toString(musicObject.getMusicID());
				ResultSet rs = st.executeQuery(queryCheck);
				int columns = rs.getMetaData().getColumnCount();
				
				if (favoriteLabel.getIcon() == emptyHeart)
				{
					//System.out.println("should be here");
					favoriteLabel.setIcon(fullHeart);
			//		if (!musicObject.getFavoritedBool())
			//		{

							if (!rs.next()) 
							{
								try
								{
									//inserting into favorited_songs table
									PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO favorite_songs (user_id, song_id)" + "VALUES (?, ?)");
									ps.setInt(1, LoggedInDriverGUI.userID);
									ps.setInt(2, musicObject.getMusicID());
									ps.executeUpdate();
									ps.close();
									//inserting into activity_feed table
									PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
									ps1.setInt(1, LoggedInDriverGUI.userID);
									ps1.setString(2, "favorite");
									java.util.Date utilDate = new java.util.Date();
								    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
								    ps1.setInt(3, musicObject.getMusicID());
								    ps1.setTimestamp(4, sqlDate);
									ps1.executeUpdate();
									ps1.close();
								} 
								catch (SQLException e1)
								{
									e1.printStackTrace();
								}
							}			
				//		musicObject.setFavoritedBool(true);
				//	}
				}
				else
				{
					//System.out.println("should not be here");
					favoriteLabel.setIcon(emptyHeart);
			//		if (musicObject.getFavoritedBool())
			//		{
							if (rs.next()) 
							{
								try
								{
									PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("DELETE FROM favorite_songs WHERE " + "user_id = ?" + " and " + "song_id = ?");
									ps.setInt(1, LoggedInDriverGUI.userID);
									ps.setInt(2, musicObject.getMusicID());
									ps.executeUpdate();
									ps.close();
								} 
								catch (SQLException e1)
								{
									e1.printStackTrace();
								}
								
							}
				//		musicObject.setFavoritedBool(false);
				//	}
				}
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
			}
			
		});
		favoriteButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				removePanel();
				currentPanel = 2;
				tabPanelMain.add(favoritePanel);
				//mainPanel.add(musicPlayerTopListened);
				tabPanelMain.revalidate();
	            tabPanelMain.repaint();
	            try
				{
					//ConnectionClass.conn = DriverManager.getConnection("jdbc:mysql://104.236.176.180/cs201", "cs201", "manishhostage");
					
					Statement st = ConnectionClass.conn.createStatement();
					//PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("SELECT song_id FROM favorite_songs WHERE user_id = " + Integer.toString(LoggedInDriverGUI.userID));
					String queryCheck = "SELECT song_id FROM favorite_songs WHERE user_id = " + Integer.toString(LoggedInDriverGUI.userID) + " AND song_id = " + Integer.toString(musicObject.getMusicID());
					ResultSet rs = st.executeQuery(queryCheck);
					int columns = rs.getMetaData().getColumnCount();
					if (rs.next())
					{
						favoriteLabel.setIcon(fullHeart);
					}
					else
					{
						favoriteLabel.setIcon(emptyHeart);
					}
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
				
			}
		});
		
		rateButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				removePanel();
				currentPanel = 1;
				tabPanelMain.add(ratePanel, BorderLayout.SOUTH);
				//mainPanel.add(musicPlayerTopListened);
				tabPanelMain.revalidate();
	            tabPanelMain.repaint();
				
			}
		});
		
		commentButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				removePanel();
				currentPanel = 0;
				tabPanelMain.add(commentPanel);
				//mainPanel.add(musicPlayerTopListened);
				tabPanelMain.revalidate();
	            tabPanelMain.repaint();
				
			}
		});
		
		oneStar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				oneStar.setIcon(fullStar);
				twoStar.setIcon(emptyStar);
				threeStar.setIcon(emptyStar);
				fourStar.setIcon(emptyStar);
				fiveStar.setIcon(emptyStar);
				myRating = 1;
				
				try
				{
					PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
					ps.setInt(1, LoggedInDriverGUI.userID);
					ps.setString(2, "rate");
					java.util.Date utilDate = new java.util.Date();
				    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
				    ps.setInt(3, musicObject.getMusicID());
				    ps.setTimestamp(4, sqlDate);
					ps.executeUpdate();
					ps.close();
					beingPlayed = true;
					//update ratings
					PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_of_ratings= ?, rating_sum= ? " + "WHERE idmusic_table = ?");
					ps1.setInt(1, musicObject.getNumberOfRatings()+1);
					ps1.setInt(2, musicObject.getRatingSum()+1);
					ps1.setInt(3, musicObject.getMusicID());
					ps1.executeUpdate();
					ps1.close();
					musicObject.setNumberOfRatings(musicObject.getNumberOfRatings()+1);
					musicObject.setRatingSum(musicObject.getRatingSum()+1);
					double rate = musicObject.getRatingSum()/musicObject.getNumberOfRatings();
					setRating(rate);
				} 
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
			}
			
		});
		
		twoStar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				oneStar.setIcon(fullStar);
				twoStar.setIcon(fullStar);
				threeStar.setIcon(emptyStar);
				fourStar.setIcon(emptyStar);
				fiveStar.setIcon(emptyStar);
				myRating = 2;
				
				try
				{
					PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
					ps.setInt(1, LoggedInDriverGUI.userID);
					ps.setString(2, "rate");
					java.util.Date utilDate = new java.util.Date();
				    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
				    ps.setInt(3, musicObject.getMusicID());
				    ps.setTimestamp(4, sqlDate);
					ps.executeUpdate();
					ps.close();
					beingPlayed = true;
					//update ratings
					PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_of_ratings= ?, rating_sum= ? " + "WHERE idmusic_table = ?");
					ps1.setInt(1, musicObject.getNumberOfRatings()+1);
					ps1.setInt(2, musicObject.getRatingSum()+2);
					ps1.setInt(3, musicObject.getMusicID());
					ps1.executeUpdate();
					ps1.close();
					musicObject.setNumberOfRatings(musicObject.getNumberOfRatings()+1);
					musicObject.setRatingSum(musicObject.getRatingSum()+2);
					double rate = musicObject.getRatingSum()/musicObject.getNumberOfRatings();
					setRating(rate);
				} 
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
			}
			
		});
		
		threeStar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				oneStar.setIcon(fullStar);
				twoStar.setIcon(fullStar);
				threeStar.setIcon(fullStar);
				fourStar.setIcon(emptyStar);
				fiveStar.setIcon(emptyStar);
				myRating = 3;
				
				try
				{
					PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
					ps.setInt(1, LoggedInDriverGUI.userID);
					ps.setString(2, "rate");
					java.util.Date utilDate = new java.util.Date();
				    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
				    ps.setInt(3, musicObject.getMusicID());
				    ps.setTimestamp(4, sqlDate);
					ps.executeUpdate();
					ps.close();
					beingPlayed = true;
					//update ratings
					PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_of_ratings= ?, rating_sum= ? " + "WHERE idmusic_table = ?");
					ps1.setInt(1, musicObject.getNumberOfRatings()+1);
					ps1.setInt(2, musicObject.getRatingSum()+3);
					ps1.setInt(3, musicObject.getMusicID());
					ps1.executeUpdate();
					ps1.close();
					musicObject.setNumberOfRatings(musicObject.getNumberOfRatings()+1);
					musicObject.setRatingSum(musicObject.getRatingSum()+3);
					double rate = musicObject.getRatingSum()/musicObject.getNumberOfRatings();
					setRating(rate);
				} 
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
			}
			
		});
		
		fourStar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				oneStar.setIcon(fullStar);
				twoStar.setIcon(fullStar);
				threeStar.setIcon(fullStar);
				fourStar.setIcon(fullStar);
				fiveStar.setIcon(emptyStar);
				myRating = 4;
				
				try
				{
					PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
					ps.setInt(1, LoggedInDriverGUI.userID);
					ps.setString(2, "rate");
					java.util.Date utilDate = new java.util.Date();
				    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
				    ps.setInt(3, musicObject.getMusicID());
				    ps.setTimestamp(4, sqlDate);
					ps.executeUpdate();
					ps.close();
					beingPlayed = true;
					//update ratings
					PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_of_ratings= ?, rating_sum= ? " + "WHERE idmusic_table = ?");
					ps1.setInt(1, musicObject.getNumberOfRatings()+1);
					ps1.setInt(2, musicObject.getRatingSum()+4);
					ps1.setInt(3, musicObject.getMusicID());
					ps1.executeUpdate();
					ps1.close();
					musicObject.setNumberOfRatings(musicObject.getNumberOfRatings()+1);
					musicObject.setRatingSum(musicObject.getRatingSum()+4);
					double rate = musicObject.getRatingSum()/musicObject.getNumberOfRatings();
					setRating(rate);
				} 
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
			}
			
		});
		
		fiveStar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				oneStar.setIcon(fullStar);
				twoStar.setIcon(fullStar);
				threeStar.setIcon(fullStar);
				fourStar.setIcon(fullStar);
				fiveStar.setIcon(fullStar);
				myRating = 5;
				
				try
				{
					PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
					ps.setInt(1, LoggedInDriverGUI.userID);
					ps.setString(2, "rate");
					java.util.Date utilDate = new java.util.Date();
				    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
				    ps.setInt(3, musicObject.getMusicID());
				    ps.setTimestamp(4, sqlDate);
					ps.executeUpdate();
					ps.close();
					beingPlayed = true;
					//update ratings
					PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_of_ratings= ?, rating_sum= ? " + "WHERE idmusic_table = ?");
					ps1.setInt(1, musicObject.getNumberOfRatings()+1);
					ps1.setInt(2, musicObject.getRatingSum()+5);
					ps1.setInt(3, musicObject.getMusicID());
					ps1.executeUpdate();
					ps1.close();
					musicObject.setNumberOfRatings(musicObject.getNumberOfRatings()+1);
					musicObject.setRatingSum(musicObject.getRatingSum()+5);
					double rate = musicObject.getRatingSum()/musicObject.getNumberOfRatings();
					setRating(rate);
				} 
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
			}
			
		});
		
		playButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//musicObject.resumeSong();
				if (myThread == null){
					//LEAP MOTION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					myThread = musicObject.playTheSong();
					Sample leap = new Sample();
					leap.start();
					if (beingPlayed) {}
					else {
						try
						{
							PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
							ps.setInt(1, LoggedInDriverGUI.userID);
							ps.setString(2, "listen");
							java.util.Date utilDate = new java.util.Date();
						    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
						    ps.setInt(3, musicObject.getMusicID());
						    ps.setTimestamp(4, sqlDate);
							ps.executeUpdate();
							ps.close();
							beingPlayed = true;
							//update number of listens
							PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_playe_count= ? " + "WHERE idmusic_table = ?");
							ps1.setInt(1, musicObject.getnumberOfPlayCounts()+1);
							ps1.setInt(2, musicObject.getMusicID());
							ps1.executeUpdate();
							ps1.close();
							musicObject.setnumberOfPlayCounts(musicObject.getnumberOfPlayCounts()+1);
							listens.setText("Listens: "+musicObject.getnumberOfPlayCounts());
						} 
						catch (SQLException e1)
						{
							e1.printStackTrace();
						}
					}
				}
				else
					myThread.resume();
			}
		});
		
		pauseButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if (myThread != null)
					myThread.suspend();
			}
		});
		
		forwardButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (myThread != null)
					myThread.suspend();
				JButton currentButton = allButtons.get(currentSong);
				currentButton.setForeground(FirstPageGUI.white);
				currentButton.setBackground(FirstPageGUI.darkGrey);
				jspComments.getVerticalScrollBar().setValue(0);
				if (currentSong == allSongs.size()-1)
				{
					musicObject = allSongs.get(0);
					currentSong = 0;
					JButton currentButton1 = allButtons.get(currentSong);
					currentButton1.setForeground(FirstPageGUI.darkGrey);
					currentButton1.setBackground(FirstPageGUI.white);
				}
				else
				{
					musicObject = allSongs.get(currentSong+1);
					currentSong++;
					JButton currentButton1 = allButtons.get(currentSong);
					currentButton1.setForeground(FirstPageGUI.darkGrey);
					currentButton1.setBackground(FirstPageGUI.white);
				}
				//artist.setText(musicObject.getArtistName() + " "+musicObject.getSongName());
				myThread = musicObject.playTheSong();
				try
				{
					PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
					ps.setInt(1, LoggedInDriverGUI.userID);
					ps.setString(2, "listen");
					java.util.Date utilDate = new java.util.Date();
				    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
				    ps.setInt(3, musicObject.getMusicID());
				    ps.setTimestamp(4, sqlDate);
					ps.executeUpdate();
					ps.close();
					beingPlayed = true;
					//update number of listens
					PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_playe_count= ? " + "WHERE idmusic_table = ?");
					ps1.setInt(1, musicObject.getnumberOfPlayCounts()+1);
					ps1.setInt(2, musicObject.getMusicID());
					ps1.executeUpdate();
					ps1.close();
					musicObject.setnumberOfPlayCounts(musicObject.getnumberOfPlayCounts()+1);
					listens.setText("Listens: "+musicObject.getnumberOfPlayCounts());
				} 
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
				resetStuff();
			}
			
		});
		
		backButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (myThread != null)
					myThread.suspend();
				JButton currentButton = allButtons.get(currentSong);
				currentButton.setForeground(FirstPageGUI.white);
				currentButton.setBackground(FirstPageGUI.darkGrey);
				jspComments.getVerticalScrollBar().setValue(0);
				if (currentSong == 0)
				{
					musicObject = allSongs.get(allSongs.size()-1);
					currentSong = allSongs.size()-1;
					JButton currentButton1 = allButtons.get(currentSong);
					currentButton1.setForeground(FirstPageGUI.darkGrey);
					currentButton1.setBackground(FirstPageGUI.white);
				}
				else
				{
					musicObject = allSongs.get(currentSong-1);
					currentSong--;
					JButton currentButton1 = allButtons.get(currentSong);
					currentButton1.setForeground(FirstPageGUI.darkGrey);
					currentButton1.setBackground(FirstPageGUI.white);
				}
				//artist.setText(musicObject.getArtistName() + " "+musicObject.getSongName());
				myThread = musicObject.playTheSong();
				try
				{
					PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
					ps.setInt(1, LoggedInDriverGUI.userID);
					ps.setString(2, "listen");
					java.util.Date utilDate = new java.util.Date();
				    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
				    ps.setInt(3, musicObject.getMusicID());
				    ps.setTimestamp(4, sqlDate);
					ps.executeUpdate();
					ps.close();
					beingPlayed = true;
					//update number of listens
					PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_playe_count= ? " + "WHERE idmusic_table = ?");
					ps1.setInt(1, musicObject.getnumberOfPlayCounts()+1);
					ps1.setInt(2, musicObject.getMusicID());
					ps1.executeUpdate();
					ps1.close();
					musicObject.setnumberOfPlayCounts(musicObject.getnumberOfPlayCounts()+1);
					listens.setText("Listens: "+musicObject.getnumberOfPlayCounts());
				} 
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
				resetStuff();
			}
		});	
		enter.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (comment.getText().length() > 20) {
					JOptionPane.showMessageDialog(MusicPlayer.this,
							"Number of character has to be 20 or less!",
							"Oh No!",
							JOptionPane.ERROR_MESSAGE);
				}
				else {
					try
					{
						PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO comments_table (user_id,song_id,comment)" + "VALUES (?, ?, ?)");
						ps.setInt(1, LoggedInDriverGUI.userID);
						ps.setInt(2, musicObject.getMusicID());
						ps.setString(3, comment.getText());
						ps.executeUpdate();
						ps.close();
					} 
					catch (SQLException e1)
					{
						e1.printStackTrace();
					}
				}
				
				JPanel outer = new JPanel();
				outer.setLayout(new FlowLayout(FlowLayout.LEFT));
				outer.setPreferredSize(new Dimension(2*dim.width/2, 9*dim.height/200));
				JLabel name = new JLabel();
				name.setPreferredSize(new Dimension(8*dim.width/24, 9*dim.height/200));
				name.setText("@"+LoggedInDriverGUI.username+":");
				JLabel commentLabel = new JLabel();
				commentLabel.setPreferredSize(new Dimension(4*dim.width/12, 9*dim.height/200));
				commentLabel.setText(comment.getText());
				outer.add(name);
				outer.add(commentLabel);
				comment.setText("");
				comments.add(outer);
				comments.revalidate();
				comments.repaint();
			}
		});
	}
	
	public void changeSong(MusicModel m, int currentSg)
	{
		if (myThread != null)
			myThread.suspend();
		musicObject = m;
		jspComments.getVerticalScrollBar().setValue(0);
		currentSong = currentSg;
		resetStuff();
		myThread = m.playTheSong();
		try
		{
			PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
			ps.setInt(1, LoggedInDriverGUI.userID);
			ps.setString(2, "listen");
			java.util.Date utilDate = new java.util.Date();
		    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
		    ps.setInt(3, musicObject.getMusicID());
		    ps.setTimestamp(4, sqlDate);
			ps.executeUpdate();
			ps.close();
			beingPlayed = true;
			//update number of listens
			PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_playe_count= ? " + "WHERE idmusic_table = ?");
			ps1.setInt(1, musicObject.getnumberOfPlayCounts()+1);
			ps1.setInt(2, musicObject.getMusicID());
			ps1.executeUpdate();
			ps1.close();
			musicObject.setnumberOfPlayCounts(musicObject.getnumberOfPlayCounts()+1);
			listens.setText("Listens: "+musicObject.getnumberOfPlayCounts());
		} 
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
	}
	
	private void resetStuff()
	{
		artist.setText(musicObject.getArtistName() + " "+musicObject.getSongName());
		try
		{
			URL imageurl = new URL(musicObject.getAlbumPath());
			BufferedImage img = ImageIO.read(imageurl);
			ImageIcon icon = new ImageIcon(img);
			Image ResizedImage = icon.getImage().getScaledInstance(3*dim.width/4, 3*dim.width/4, Image.SCALE_SMOOTH);
			album.setIcon(new ImageIcon(ResizedImage));
		} catch (IOException e1)
		{
			
		}
		oneStar.setIcon(emptyStar);
		twoStar.setIcon(emptyStar);
		threeStar.setIcon(emptyStar);
		fourStar.setIcon(emptyStar);
		fiveStar.setIcon(emptyStar);
		//favoriteLabel.setIcon(emptyHeart);
		
		try
		{
			//ConnectionClass.conn = DriverManager.getConnection("jdbc:mysql://104.236.176.180/cs201", "cs201", "manishhostage");
			
			Statement st = ConnectionClass.conn.createStatement();
			//PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("SELECT song_id FROM favorite_songs WHERE user_id = " + Integer.toString(LoggedInDriverGUI.userID));
			String queryCheck = "SELECT song_id FROM favorite_songs WHERE user_id = " + Integer.toString(LoggedInDriverGUI.userID) + " AND song_id = " + Integer.toString(musicObject.getMusicID());
			ResultSet rs = st.executeQuery(queryCheck);
			int columns = rs.getMetaData().getColumnCount();
			if (rs.next())
			{
				favoriteLabel.setIcon(fullHeart);
			}
			else
			{
				favoriteLabel.setIcon(emptyHeart);
			}
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
		comments.removeAll();
		String query = "SELECT * from comments_table WHERE song_id= " + Integer.toString(musicObject.getMusicID());
		try {
			Statement st = ConnectionClass.conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			int columns = rs.getMetaData().getColumnCount();
//			Vector<Integer> userIDVector = new Vector<Integer> ();
//			Vector<String> commentVector = new Vector<String> ();
			while (rs.next())
			{
				int ID = 0;
				String comment1;
				
				ID = rs.getInt(1);
				comment1 = rs.getString(3);
				System.out.println("Comment: "+comment1);
				
				String query1 = "Select username from user_table where iduser_table = " + Integer.toString(ID);
				Statement st1 = ConnectionClass.conn.createStatement();
				ResultSet rs1 = st1.executeQuery(query1);
				int columns1 = rs1.getMetaData().getColumnCount();
				while (rs1.next()){
					JPanel outer = new JPanel();
					outer.setLayout(new FlowLayout(FlowLayout.LEFT));
					outer.setPreferredSize(new Dimension(2*dim.width/2, 9*dim.height/200));
					JLabel name = new JLabel();
					name.setPreferredSize(new Dimension(8*dim.width/24, 9*dim.height/200));
					name.setText("@"+rs1.getString(1)+":");
					JLabel commentLabel = new JLabel();
					commentLabel.setPreferredSize(new Dimension(4*dim.width/12, 9*dim.height/200));
					commentLabel.setText(comment1);
					outer.add(name);
					outer.add(commentLabel);
					comments.add(outer);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		comments.revalidate();
		comments.repaint();
		
		double rate = musicObject.getRatingSum()/musicObject.getNumberOfRatings();
		int listens1 = musicObject.getnumberOfPlayCounts();
		listens.setText("#Listens: "+listens1);
		setRating(rate);
		mainPanel.revalidate();
		mainPanel.repaint();
		
	}
	
	private void setRating(double rate)
	{
		rating.setText("Overall Rating: "+rate+"  ");
		ratingPanel.removeAll();
		ratingPanel.add(rating);
		int i = 0;
		//System.out.println(rate);
		if (rate <= 1.4 && rate>.9)
		{
			i = 1;
		}
		else if (rate <=2.4 && rate>1.4)
		{	
			i=2;
		}
		else if (rate <=3.4 && rate>2.4)
		{
			i =3;
		}
		else if (rate <= 4.4 && rate > 3.4)
		{
			i = 4;
		}
		else if (rate >4.4)
		{
			i=5;
		}
		if (i!= 0)
		{
			for (int j = 1; j<=i; j++)
			{
				JLabel temp = new JLabel("");
				temp.setIcon(new ImageIcon("data/star2.png"));
				ratingPanel.add(temp);
			}
		}
		ratingPanel.revalidate();
		ratingPanel.repaint();
	}
	private void removePanel()
	{
			if (currentPanel == 0) 
			{
				tabPanelMain.remove(commentPanel);
			}
			else if (currentPanel == 1)
			{
				tabPanelMain.remove(ratePanel);
			}
			else if (currentPanel == 2)
			{
				tabPanelMain.remove(favoritePanel);
			}
	}
	
	public Thread getCurrentThread()
	{
		return myThread;
	}
	
	
	///////////////========================================================================START
	class SampleListener extends Listener {
	    public void onInit(Controller controller) {
	        System.out.println("Initialized");
	    }

	    public void onConnect(Controller controller) {
	        System.out.println("Connected");
	        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
	        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
	        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
	        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
	    }

	    public void onDisconnect(Controller controller) {
	        //Note: not dispatched when running in a debugger.
	        System.out.println("Disconnected");
	    }

	    public void onExit(Controller controller) {
	        System.out.println("Exited");
	    }
	    public void onFrame(Controller controller) {
	        // Get the most recent frame and report some basic information
	        Frame frame = controller.frame();
//	        System.out.println("Frame id: " + frame.id()
//	                         + ", timestamp: " + frame.timestamp()
//	                         + ", hands: " + frame.hands().count()
//	                         + ", fingers: " + frame.fingers().count()
//	                         + ", tools: " + frame.tools().count()
//	                         + ", gestures " + frame.gestures().count());


	        GestureList gestures = frame.gestures();
	        
	        if(frame.gestures().count()>0){
	        	stopThread();
	        	//System.out.println("FOUND A GESTURE");
	        }
	        
	        
	        for (int i = 0; i < gestures.count(); i++) {
	            Gesture gesture = gestures.get(i);

	            switch (gesture.type()) {
	                case TYPE_CIRCLE:
	                    CircleGesture circle = new CircleGesture(gesture);

	                    // Calculate clock direction using the angle between circle normal and pointable
	                    String clockwiseness;
	                    if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {
	                        // Clockwise if angle is less than 90 degrees
	                        clockwiseness = "clockwise";
	                        System.out.println(clockwiseness);
	                        if (myThread != null)
	        					myThread.suspend();
	        				JButton currentButton = allButtons.get(currentSong);
	        				currentButton.setForeground(FirstPageGUI.white);
	        				currentButton.setBackground(FirstPageGUI.darkGrey);
	        				jspComments.getVerticalScrollBar().setValue(0);
	        				if (currentSong == allSongs.size()-1)
	        				{
	        					musicObject = allSongs.get(0);
	        					currentSong = 0;
	        					JButton currentButton1 = allButtons.get(currentSong);
	        					currentButton1.setForeground(FirstPageGUI.darkGrey);
	        					currentButton1.setBackground(FirstPageGUI.white);
	        				}
	        				else
	        				{
	        					musicObject = allSongs.get(currentSong+1);
	        					currentSong++;
	        					JButton currentButton1 = allButtons.get(currentSong);
	        					currentButton1.setForeground(FirstPageGUI.darkGrey);
	        					currentButton1.setBackground(FirstPageGUI.white);
	        				}
	        				resetStuff();
	        				//artist.setText(musicObject.getArtistName() + " "+musicObject.getSongName());
	        				myThread = musicObject.playTheSong();
	        				try
	        				{
	        					PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
	        					ps.setInt(1, LoggedInDriverGUI.userID);
	        					ps.setString(2, "listen");
	        					java.util.Date utilDate = new java.util.Date();
	        				    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
	        				    ps.setInt(3, musicObject.getMusicID());
	        				    ps.setTimestamp(4, sqlDate);
	        					ps.executeUpdate();
	        					ps.close();
	        					beingPlayed = true;
	        					//update number of listens
	        					PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_playe_count= ? " + "WHERE idmusic_table = ?");
	        					ps1.setInt(1, musicObject.getnumberOfPlayCounts()+1);
	        					ps1.setInt(2, musicObject.getMusicID());
	        					ps1.executeUpdate();
	        					ps1.close();
	        					musicObject.setnumberOfPlayCounts(musicObject.getnumberOfPlayCounts()+1);
	        					listens.setText("Listens: "+musicObject.getnumberOfPlayCounts());
	        				} 
	        				catch (SQLException e1)
	        				{
	        					e1.printStackTrace();
	        				}
	                    } else {
	                        clockwiseness = "counterclockwise";
	                        System.out.println(clockwiseness);
	                        
	                        
	                        
	                        if (myThread != null)
	        					myThread.suspend();
	        				JButton currentButton = allButtons.get(currentSong);
	        				currentButton.setForeground(FirstPageGUI.white);
	        				currentButton.setBackground(FirstPageGUI.darkGrey);
	        				jspComments.getVerticalScrollBar().setValue(0);
	        				if (currentSong == 0)
	        				{
	        					musicObject = allSongs.get(allSongs.size()-1);
	        					currentSong = allSongs.size()-1;
	        					JButton currentButton1 = allButtons.get(currentSong);
	        					currentButton1.setForeground(FirstPageGUI.darkGrey);
	        					currentButton1.setBackground(FirstPageGUI.white);
	        				}
	        				else
	        				{
	        					musicObject = allSongs.get(currentSong-1);
	        					currentSong--;
	        					JButton currentButton1 = allButtons.get(currentSong);
	        					currentButton1.setForeground(FirstPageGUI.darkGrey);
	        					currentButton1.setBackground(FirstPageGUI.white);
	        				}
	        				resetStuff();
	        				//artist.setText(musicObject.getArtistName() + " "+musicObject.getSongName());
	        				myThread = musicObject.playTheSong();
	        				try
	        				{
	        					PreparedStatement ps = (PreparedStatement) ConnectionClass.conn.prepareStatement("INSERT INTO activity_feed (user_id,description,song_id,time_stamp)" + "VALUES (?, ?, ?, ?)");
	        					ps.setInt(1, LoggedInDriverGUI.userID);
	        					ps.setString(2, "listen");
	        					java.util.Date utilDate = new java.util.Date();
	        				    java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
	        				    ps.setInt(3, musicObject.getMusicID());
	        				    ps.setTimestamp(4, sqlDate);
	        					ps.executeUpdate();
	        					ps.close();
	        					beingPlayed = true;
	        					//update number of listens
	        					PreparedStatement ps1 = (PreparedStatement) ConnectionClass.conn.prepareStatement("UPDATE music_table SET numb_playe_count= ? " + "WHERE idmusic_table = ?");
	        					ps1.setInt(1, musicObject.getnumberOfPlayCounts()+1);
	        					ps1.setInt(2, musicObject.getMusicID());
	        					ps1.executeUpdate();
	        					ps1.close();
	        					musicObject.setnumberOfPlayCounts(musicObject.getnumberOfPlayCounts()+1);
	        					listens.setText("Listens: "+musicObject.getnumberOfPlayCounts());
	        				} 
	        				catch (SQLException e1)
	        				{
	        					e1.printStackTrace();
	        				}
	                    }

	                    // Calculate angle swept since last frame
	                    double sweptAngle = 0;
	                    if (circle.state() != State.STATE_START) {
	                        CircleGesture previousUpdate = new CircleGesture(controller.frame(1).gesture(circle.id()));
	                        sweptAngle = (circle.progress() - previousUpdate.progress()) * 2 * Math.PI;
	                    }

//	                    System.out.println("Circle id: " + circle.id()
//	                               + ", " + circle.state()
//	                               + ", progress: " + circle.progress()
//	                               + ", radius: " + circle.radius()
//	                               + ", angle: " + Math.toDegrees(sweptAngle)
//	                               + ", " + clockwiseness);
	                    break;
	                case TYPE_SWIPE:
	                    SwipeGesture swipe = new SwipeGesture(gesture);
//	                    System.out.println("Swipe id: " + swipe.id()
//	                               + ", " + swipe.state()
//	                               + ", position: " + swipe.position()
//	                               + ", direction: " + swipe.direction()
//	                               + ", speed: " + swipe.speed());
	                    //resumeThread();
	                    stopThread();
	                    break;
	                case TYPE_SCREEN_TAP:
	                    ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
//	                    System.out.println("Screen Tap id: " + screenTap.id()
//	                               + ", " + screenTap.state()
//	                               + ", position: " + screenTap.position()
//	                               + ", direction: " + screenTap.direction());
	                    resumeThread();
	                    break;
	                case TYPE_KEY_TAP:
	                    KeyTapGesture keyTap = new KeyTapGesture(gesture);
//	                    System.out.println("Key Tap id: " + keyTap.id()
//	                               + ", " + keyTap.state()
//	                               + ", position: " + keyTap.position()
//	                               + ", direction: " + keyTap.direction());
	                    stopThread();
	                    break;
	                default:
	                    System.out.println("Unknown gesture type.");
	                    break;
	            }
	        }

	        if (!frame.hands().isEmpty() || !gestures.isEmpty()) {
	            System.out.println();
	        }
	    }
	}
	//////////////=========================================================================END
	class Sample extends Thread{
	    public void run() {
	        // Create a sample listener and controller
	        SampleListener listener = new SampleListener();
	        Controller controller = new Controller();

	        // Have the sample listener receive events from the controller
	        controller.addListener(listener);

	        // Keep this process running until Enter is pressed
	        System.out.println("Press Enter to quit...");
	        try {
	            System.in.read();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        // Remove the sample listener when done
	        controller.removeListener(listener);
	    }
	}

}