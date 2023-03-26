package com.nrifintech.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.Issue;
import com.nrifintech.model.User;
import com.nrifintech.repository.IssuePerAuthor;
import com.nrifintech.repository.IssuePerBook;
import com.nrifintech.repository.IssuePerGenre;
import com.nrifintech.repository.IssueRepository;

@Service
public class IssueService 
{
	
	@Autowired
	private IssueRepository issueRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private BookService bookService;
	
	@Autowired
	private JavaMailSender javamailsender;
	
	@Value("${spring.mail.username}")
	private String sendermail;
	
	private int rownum,cellnum;
	
	private XSSFWorkbook workbook;
	
	private XSSFSheet sheet;
	
	private HttpHeaders header;

	// get all issues list
	public List<Issue> getAllIssues() {

		List<Issue> issues = new ArrayList<>();
		for (Issue i : issueRepo.findAll())
			issues.add(i);
		return issues;
	}

	// add a new issue
	public ResponseEntity<Issue> addIssue(Issue issue) throws ResourceNotFoundException {
		issue.setUser(userService.getUserByusername(issue.getUser().getUsername()).getBody());
		issue.setBook(bookService.getBookByTitle(issue.getBook().getTitle()).getBody().get(0));
		issue.setFine(updateFineByIssue(issue.getIssueDate(), issue.getFine()));
		System.out.println(issue);
		issueRepo.save(issue);
		return ResponseEntity.ok().body(issue);
	}
	
	public ResponseEntity<Issue> getEarliestAvailableIssuePerBook(int bookId) throws ResourceNotFoundException {
		Issue issue = issueRepo.findEarliestAvailableDateOfABook(bookId);
		return ResponseEntity.ok().body(issue);
	}

	// delete a issue
	public ResponseEntity<Issue> deleteIssue(int issueId) throws ResourceNotFoundException {
		Issue issue = issueRepo.findById(issueId)
				.orElseThrow(() -> new ResourceNotFoundException("Issue not found for this id " + issueId));
		issueRepo.deleteById(issueId);
		return ResponseEntity.ok().body(issue);
	}

	// get issue by a issueId
	public ResponseEntity<Issue> getIssueByIssueId(int issueId) throws ResourceNotFoundException {
		Issue issue = issueRepo.findById(issueId)
				.orElseThrow(() -> new ResourceNotFoundException("Issue not found for this id " + issueId));
		return ResponseEntity.ok().body(issue);
	}

	// update issue by IssueId
	public ResponseEntity<Issue> updateIssue(Issue inew, int issueId) throws ResourceNotFoundException {
		Issue issue = issueRepo.findById(issueId)
				.orElseThrow(() -> new ResourceNotFoundException("Issue not found for this id " + issueId));
		issue.setBook(bookService.getBookByTitle(inew.getBook().getTitle()).getBody().get(0));
		issue.setStatus(inew.getStatus());
		issue.setIssueDate(inew.getIssueDate());
		issue.setUser(userService.getUserByusername(inew.getUser().getUsername()).getBody());
		issue.setFine(updateFineByIssue(inew.getIssueDate(), inew.getFine()));
		issueRepo.save(issue);
		return ResponseEntity.ok().body(issue);
	}

	// Get Issues By Book
	public ResponseEntity<List<Issue>> getIssueByBook(String bookTitle) {
		List<Issue> issuelist = new ArrayList<Issue>();
		for (Issue is : issueRepo.findAll()) {
			if (is.getBook().getTitle().equalsIgnoreCase(bookTitle)) {
				issuelist.add(is);
			}
		}
		return ResponseEntity.ok().body(issuelist);
	}

	// Get Issues By Username
	public ResponseEntity<List<Issue>> getIssueByUser(String username) {
		List<Issue> issuelist = new ArrayList<Issue>();

		for (Issue is : issueRepo.findAll()) {
			if (is.getUser().getUsername().equals(username)) {
				issuelist.add(is);
			}
		}
		return ResponseEntity.ok().body(issuelist);
	}

	// Get issues By Date
	public ResponseEntity<List<Issue>> getIssueByDate(String Date) {
		List<Issue> issuelist = new ArrayList<Issue>();

		for (Issue is : issueRepo.findAll()) {
			if (is.getIssueDate().equals(Date)) {
				issuelist.add(is);
			}
		}
		return ResponseEntity.ok().body(issuelist);
	}

	

	// get issue by status
	public List<Issue> getIssueByStatus(String status) {
		List<Issue> issues = new ArrayList<>();
		issues = issueRepo.findIssueAllByStatus(status);
		return issues;
	}

	// get issue by userName and status
	public List<Issue> getIssueByUserNameAndStatus(String userName, String status) {
		List<Issue> issues = new ArrayList<>();
		issues = issueRepo.findIssueAllByUserNameAndStatus(userName, status);
//		System.out.println(issues);
		return issues;
	}

	// get issue by bookTitle and userName
	public List<Issue> getIssueByTitleAndUserNameAndStatus(String title, String status,String userName) {
		List<Issue> issues = new ArrayList<>();
		issues = issueRepo.findIssueAllByTitleAndUserNameAndStatus(title, status,userName);
		return issues;
	}

	public long daysCalculation(String isDate) 
	{
		long milliSeconds = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date issueDate = new Date();
		try {
			issueDate = sdf.parse(isDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date currentDate = new Date(milliSeconds);
		System.out.println(issueDate);
		System.out.println(currentDate);
		long time_diff = currentDate.getTime() - issueDate.getTime();
		long days_diff = TimeUnit.MILLISECONDS.toDays(time_diff) % 365;
		System.out.println(days_diff);
		return days_diff;
	}
	
	public int updateFineByIssue(String date,int fine)
	{
		long days_diff=daysCalculation(date);
		Calendar cal=Calendar.getInstance();
		if (cal.get(Calendar.DATE)==28) 
		{
			fine=0;
		}
		if (days_diff> 10) 
		{
			fine+=10;
		}
		return fine;
	}
	
	public void updateFine()
	{
		for(Issue i:issueRepo.findAll())
		{
			if(i.getStatus().equalsIgnoreCase("Issued"))
			{
				long days_diff=daysCalculation(i.getIssueDate());
				Calendar cal=Calendar.getInstance();
				if (i.getFine() == null||cal.get(Calendar.DATE)==28) 
				{
					i.setFine(0);
				}
				if (days_diff> 10) 
				{
					i.setFine(i.getFine()+10);
				}
				issueRepo.save(i);
			}
		}
		System.out.println("All Issues Fine Updated");
	}
	
	
	public Integer updateTotalFineByUsername(String username) 
	{
		Integer Totalfine=0;
		for (Issue i : issueRepo.findAll()) 
		{
			if (i.getUser().getUsername().equals(username) && i.getStatus().equalsIgnoreCase("Issued"))
			{
				long days_diff=daysCalculation(i.getIssueDate());
				Calendar cal=Calendar.getInstance();
				if (i.getFine() == null||cal.get(Calendar.DATE)==28) 
				{
					Totalfine += 0;
				}
				if (days_diff> 10) 
				{
					Totalfine += 10;
				}
			}
		}
		return Totalfine;
	}
	
	@Scheduled(cron="${cron.expression.value}")
	public void UpdateUserFine() throws ResourceNotFoundException
	{
		updateFine();
		for(User u:userService.getAllUsers())
		{
			Calendar cal=Calendar.getInstance();
			if(cal.get(Calendar.DATE)==28)
			{
				u.setFine(0);
			}
			u.setFine(u.getFine()+updateTotalFineByUsername(u.getUsername()));
			userService.updateUser(u.getId(), u);
			System.out.println("User Fine Updated "+u.getUsername());
		}
	}
	
	
	
	public ResponseEntity<List<Issue>> getFineDetails() 
	{
		List<Issue> fineList = new ArrayList<Issue>();

		for (Issue i : issueRepo.findAll()) 
		{
			if (i.getStatus().equalsIgnoreCase("Issued"))
			{
				fineList.add(i);
			}
		}
		return ResponseEntity.ok().body(fineList);
	}

	public ResponseEntity<List<Issue>> getfineDetailsByUsername(String username) throws ResourceNotFoundException 
	{
		List<Issue> userIssueList = new ArrayList<Issue>();
		for (Issue i : issueRepo.findAll()) 
		{
			if (i.getUser().getUsername().equals(username) && i.getStatus().equalsIgnoreCase("Issued")) 
			{
				userIssueList.add(i);
			}
		}
		return ResponseEntity.ok().body(userIssueList);
	}

	
	
	public ResponseEntity<String> updateFineByIssueId(int IssueId)
	{
		Issue i=issueRepo.findById(IssueId).get();
		if ( i.getStatus().equalsIgnoreCase("Issued")) 
		{
			i.setFine(0);
			issueRepo.save(i);
			return ResponseEntity.ok().body("Fine Updated");
		}
		return ResponseEntity.ok().body("Invalid IssueId");
	}
	
	//Create Sheet
	public void createSheet()
	{
		header=new HttpHeaders();
		header.setContentType(new MediaType("application","force-download"));
		header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=IssuesReport.xlsx");
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Issues");
		rownum = 1;
		Row row = sheet.createRow(rownum++);
		XSSFFont font=workbook.createFont();
		font.setBold(true);
		font.setFontName("TimesNewRoman" );
		font.setFontHeight(18.0);
		CellStyle style=workbook.createCellStyle();
		style.setFont(font);
		cellnum = 1;
		Cell cellidName = row.createCell(cellnum++);
		cellidName.setCellValue("IssueId");
		cellidName.setCellStyle(style);
		Cell cellissueDateName = row.createCell(cellnum++);
		cellissueDateName.setCellValue("IssueDate");
		cellissueDateName.setCellStyle(style);
		Cell cellBookName = row.createCell(cellnum++);
		cellBookName.setCellValue("Book");
		cellBookName.setCellStyle(style);
		Cell cellUserName = row.createCell(cellnum++);
		cellUserName.setCellValue("User");
		cellUserName.setCellStyle(style);
		Cell cellStatusName = row.createCell(cellnum++);
		cellStatusName.setCellValue("Status");
		cellStatusName.setCellStyle(style);
		Cell cellfineName = row.createCell(cellnum++);
		cellfineName.setCellValue("Fine");
		cellfineName.setCellStyle(style);
	}
		
	//Create Data in sheet
	public void createDataInSheet(Issue is)
	{
		XSSFFont fontData=workbook.createFont();
		fontData.setBold(false);
		fontData.setFontName("TimesNewRoman" );
		fontData.setFontHeight(16.0);
		CellStyle styleData=workbook.createCellStyle();
		styleData.setFont(fontData);
		cellnum = 1;
		Row rowvalues = sheet.createRow(rownum++);
		Cell cellissueId = rowvalues.createCell(cellnum++);
		cellissueId.setCellValue(is.getIssueId());
		cellissueId.setCellStyle(styleData);
		Cell cellDate = rowvalues.createCell(cellnum++);
		cellDate.setCellValue(is.getIssueDate());
		cellDate.setCellStyle(styleData);
		Cell cellTitle = rowvalues.createCell(cellnum++);
		cellTitle.setCellValue(is.getBook().getTitle());
		cellTitle.setCellStyle(styleData);
		Cell cellUser = rowvalues.createCell(cellnum++);
		cellUser.setCellValue(is.getUser().getUsername());
		cellUser.setCellStyle(styleData);
		Cell cellStatus = rowvalues.createCell(cellnum++);
		cellStatus.setCellValue(is.getStatus());
		cellStatus.setCellStyle(styleData);
		Cell cellfine = rowvalues.createCell(cellnum++);
		cellfine.setCellValue(is.getFine());
		cellfine.setCellStyle(styleData);
	}
	
	public void adjustSize(XSSFSheet sheet)
    {
		for(int i=0;i<sheet.getPhysicalNumberOfRows();i++)
		{
			Row row =sheet.getRow(i+1);
			for(int j=0;j<row.getPhysicalNumberOfCells();j++)
			{
				sheet.autoSizeColumn(j+1);
			}
		}
		
	}
		
	// Report of all Users
	public ResponseEntity<ByteArrayResource> generateReport() 
	{
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		createSheet();
		for (Issue is : issueRepo.findAll()) 
		{
			createDataInSheet(is);
		}
		try
		{
			adjustSize(workbook.getSheet("Issues"));
			workbook.write(bs);
			bs.close();
			workbook.close();
		} 
		catch (IOException e)
		{
						System.out.println(e.getMessage());
		}
		return new ResponseEntity<>(new ByteArrayResource(bs.toByteArray()),header,HttpStatus.CREATED);
	}

    

	// Report of Particular User
	public ResponseEntity<ByteArrayResource> generateReportByUser(int userId) 
    {
		ByteArrayOutputStream bs=new ByteArrayOutputStream();
		createSheet();
		for (Issue is : issueRepo.findAll()) 
		{
			if (is.getUser().getId() == userId) 
			{
				createDataInSheet(is);
			}
		}
		try
		{
			adjustSize(workbook.getSheet("Issues"));
			workbook.write(bs);
			bs.close();
			workbook.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		return new ResponseEntity<>(new ByteArrayResource(bs.toByteArray()),header,HttpStatus.CREATED);
	}
	
	//Report for Books and total issues
		public ResponseEntity<ByteArrayResource> generateReportForBookUsage()
		{
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			header=new HttpHeaders();
			header.setContentType(new MediaType("application","force-download"));
			header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Issue_per_book_report.xlsx");
			workbook = new XSSFWorkbook();
			XSSFFont font=workbook.createFont();
			font.setBold(true);
			font.setFontName("TimesNewRoman" );
			font.setFontHeight(18.0);
			CellStyle style=workbook.createCellStyle();
			style.setFont(font);
			sheet = workbook.createSheet("IssuesPerBook");
			int rownum = 1;
			Row row = sheet.createRow(rownum++);
			int cellnum = 1;
			Cell cellBookId = row.createCell(cellnum++);
			cellBookId.setCellValue("BookId");
			cellBookId.setCellStyle(style);
			Cell cellBookName = row.createCell(cellnum++);
			cellBookName.setCellValue("Book");
			cellBookName.setCellStyle(style);
			Cell cellTotalIssue = row.createCell(cellnum++);
			cellTotalIssue.setCellValue("Total Issues");
			cellTotalIssue.setCellStyle(style);
			XSSFFont fontData=workbook.createFont();
			fontData.setBold(false);
			fontData.setFontName("TimesNewRoman" );
			fontData.setFontHeight(16.0);
			CellStyle styleData=workbook.createCellStyle();
			styleData.setFont(fontData);
			for(IssuePerBook ipb: issueRepo.countTotalIssuePerBook())
			{
				cellnum = 1;
				Row rowvalues = sheet.createRow(rownum++);
				Cell cellFirst = rowvalues.createCell(cellnum++);
				cellFirst.setCellValue(ipb.getBookId());
				cellFirst.setCellStyle(styleData);
				Cell cellSecond = rowvalues.createCell(cellnum++);
				cellSecond.setCellValue(ipb.getTitle());
				cellSecond.setCellStyle(styleData);
				Cell cellThird = rowvalues.createCell(cellnum++);
				cellThird.setCellValue(ipb.getTotalIssue());
				cellThird.setCellStyle(styleData);
			}
			try {
				adjustSize(workbook.getSheet("IssuesPerBook"));
				workbook.write(bs);
				bs.close();
				workbook.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			return new ResponseEntity<>(new ByteArrayResource(bs.toByteArray()),header,HttpStatus.CREATED);
			
			
		}
		
		//Report for Author and total issues
		public ResponseEntity<ByteArrayResource> generateReportForAuthorPopularity()
		{
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			header=new HttpHeaders();
			header.setContentType(new MediaType("application","force-download"));
			header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Issue_per_author_report.xlsx");
			workbook = new XSSFWorkbook();
			XSSFFont font=workbook.createFont();
			font.setBold(true);
			font.setFontName("TimesNewRoman" );
			font.setFontHeight(18.0);
			CellStyle style=workbook.createCellStyle();
			style.setFont(font);
			XSSFFont fontData=workbook.createFont();
			fontData.setBold(false);
			fontData.setFontName("TimesNewRoman" );
			fontData.setFontHeight(16.0);
			CellStyle styleData=workbook.createCellStyle();
			styleData.setFont(fontData);
			sheet = workbook.createSheet("IssuesPerAuthor");
			int rownum = 1;
			Row row = sheet.createRow(rownum++);
			int cellnum = 1;
			Cell cellAuthorId = row.createCell(cellnum++);
			cellAuthorId.setCellValue("AuthorId");
			cellAuthorId.setCellStyle(style);
			Cell cellAuthorName = row.createCell(cellnum++);
			cellAuthorName.setCellValue("Author");
			cellAuthorName.setCellStyle(style);
			Cell cellTotalIssue = row.createCell(cellnum++);
			cellTotalIssue.setCellValue("Total Issues");
			cellTotalIssue.setCellStyle(style);
			for(IssuePerAuthor ipa: issueRepo.countTotalIssuePerAuthor())
			{
				cellnum = 1;
				Row rowvalues = sheet.createRow(rownum++);
				Cell cellFirst = rowvalues.createCell(cellnum++);
				cellFirst.setCellValue(ipa.getAuthorId());
				cellFirst.setCellStyle(styleData);
				Cell cellSecond = rowvalues.createCell(cellnum++);
				cellSecond.setCellValue(ipa.getAuthorName());
				cellSecond.setCellStyle(styleData);
				Cell cellThird = rowvalues.createCell(cellnum++);
				cellThird.setCellValue(ipa.getTotalIssue());
				cellThird.setCellStyle(styleData);
			}
			try {
				adjustSize(workbook.getSheet("IssuesPerAuthor"));
				workbook.write(bs);
				bs.close();
				workbook.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			return new ResponseEntity<>(new ByteArrayResource(bs.toByteArray()),header,HttpStatus.CREATED);
			
			
		}
		
		//Report for Genre and total issues
			public ResponseEntity<ByteArrayResource> generateReportForGenrePopularity()
			{
				ByteArrayOutputStream bs = new ByteArrayOutputStream();
				header=new HttpHeaders();
				header.setContentType(new MediaType("application","force-download"));
				header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Issue_per_genre_report.xlsx");
				workbook = new XSSFWorkbook();
				XSSFFont font=workbook.createFont();
				font.setBold(true);
				font.setFontName("TimesNewRoman" );
				font.setFontHeight(18.0);
				CellStyle style=workbook.createCellStyle();
				style.setFont(font);
				XSSFFont fontData=workbook.createFont();
				fontData.setBold(false);
				fontData.setFontName("TimesNewRoman" );
				fontData.setFontHeight(16.0);
				CellStyle styleData=workbook.createCellStyle();
				styleData.setFont(fontData);
				sheet = workbook.createSheet("IssuesPerGenre");
				int rownum = 1;
				Row row = sheet.createRow(rownum++);
				int cellnum = 1;
				Cell cellGenreId = row.createCell(cellnum++);
				cellGenreId.setCellValue("GenreId");
				cellGenreId.setCellStyle(style);
				Cell cellGenreName = row.createCell(cellnum++);
				cellGenreName.setCellValue("Genre");
				cellGenreName.setCellStyle(style);
				Cell cellTotalIssue = row.createCell(cellnum++);
				cellTotalIssue.setCellValue("Total Issues");
				cellTotalIssue.setCellStyle(style);
				for(IssuePerGenre ipg: issueRepo.countTotalIssuePerGenre())
				{
					cellnum = 1;
					Row rowvalues = sheet.createRow(rownum++);
					Cell cellFirst = rowvalues.createCell(cellnum++);
					cellFirst.setCellValue(ipg.getGenreId());
					cellFirst.setCellStyle(styleData);
					Cell cellSecond = rowvalues.createCell(cellnum++);
					cellSecond.setCellValue(ipg.getGenreName());
					cellSecond.setCellStyle(styleData);
					Cell cellThird = rowvalues.createCell(cellnum++);
					cellThird.setCellValue(ipg.getTotalIssue());
					cellThird.setCellStyle(styleData);
				}
				try {
					adjustSize(workbook.getSheet("IssuesPerGenre"));
					workbook.write(bs);
					bs.close();
					workbook.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				return new ResponseEntity<>(new ByteArrayResource(bs.toByteArray()),header,HttpStatus.CREATED);
				
				
			}
			
			public List<IssuePerBook> getIssuesPerBook()
			{
				return  issueRepo.countTotalIssuePerBook();
			}
			
			public List<IssuePerAuthor> getIssuesPerAuthor()
			{
				return  issueRepo.countTotalIssuePerAuthor();
			}
			
			public List<IssuePerGenre> getIssuesPerGenre()
			{
				return  issueRepo.countTotalIssuePerGenre();
			}
	
	@Scheduled(cron="${cron.expression.value}")
	public void sendRemainder()
	{
		for(Issue i:issueRepo.findAll())
		{
			SimpleMailMessage smg=new SimpleMailMessage();
			smg.setFrom(sendermail);
			if(i.getStatus().equalsIgnoreCase("Issued") && daysCalculation(i.getIssueDate())>=7 && daysCalculation(i.getIssueDate())<10)
			{
				smg.setTo(i.getUser().getEmail());
				smg.setSubject("Remainder");
				smg.setText("Please return the book "+i.getBook().getTitle()+"with in "+(10-daysCalculation(i.getIssueDate()))+" days to library");
				javamailsender.send(smg);
			}
			else if(i.getStatus().equalsIgnoreCase("Issued") && daysCalculation(i.getIssueDate())>10)
			{
				smg.setTo(i.getUser().getEmail());
				smg.setSubject("Remainder");
				smg.setText("Please return the book to the library"+i.getBook().getTitle()+" due date exceeded");
				javamailsender.send(smg);
			}
		}
	}
}
