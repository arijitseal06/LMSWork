package com.nrifintech.service;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.mail.internet.MimeMessage;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.ConfirmationToken;
import com.nrifintech.model.User;
import com.nrifintech.repository.ConfirmationTokenRepository;
import com.nrifintech.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JavaMailSender javamailsender;
	
	@Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    EmailService emailService;
	
	@Value("${spring.mail.username}")
	private String sendermail;
	
	BCryptPasswordEncoder bs=new BCryptPasswordEncoder();
	
	
	public User addUser(User user)
	{
		return userRepository.save(user);
	}
	

	public List<User> getAllUsers()
	{
		List<User> userList=new ArrayList<User>();
		
		for(User a:userRepository.findAll())
		{
			userList.add(a);
		}
		return userList;
	}
	
	public ResponseEntity<User> updateUser(Integer userId,User newUser) throws ResourceNotFoundException{
		User user=userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found for the id "+userId));
		user.setName(newUser.getName());
		user.setAge(newUser.getAge());
		user.setEmail(newUser.getEmail());
		user.setUsername(newUser.getUsername());
		user.setPassword(newUser.getPassword());
		user.setFine(newUser.getFine());
		userRepository.save(user);
		return ResponseEntity.ok().body(user);
	}
	
	public ResponseEntity<User> deleteUser(Integer userId) throws ResourceNotFoundException{
		User user =userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found for this id "+userId));
		userRepository.delete(user);
		return ResponseEntity.ok().body(user);
	}
	
	public ResponseEntity<User> getUserById(int userId) throws ResourceNotFoundException
	{
		User user =userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found for this id "+userId));
		return ResponseEntity.ok().body(user);
	}
	
	public ResponseEntity<User> getUserByusername(String username) throws ResourceNotFoundException
	{
		for(User user:userRepository.findAll())
		{
			if(user.getUsername().equals(username))
			{
				return ResponseEntity.ok().body(user);
			}
		}
		return ResponseEntity.ok().body(null);
	}

	public ResponseEntity<Integer> getFineByUsername(String username)
	{
		for(User u:userRepository.findAll())
		{
			if(u.getUsername().equals(username))
			{
				return ResponseEntity.ok().body(u.getFine());
			}
		}
		return ResponseEntity.ok().body(0);
	}
	
	public ResponseEntity<String> accountRecovery(String username)
	{
		int flag=1;
		String passcode="";
		Random r=new Random();
		for(User user:userRepository.findAll())
		{
			if(user.getUsername().equals(username))
			{
				for(int i=0;i<5;i++)
				{
					passcode+=r.nextInt(9);
				}
				user.setPassword(bs.encode(passcode));
				userRepository.save(user);
				String Text="This is your new password "+passcode+" Please update your password after login";
				SimpleMailMessage smg=new SimpleMailMessage();
				smg.setFrom(sendermail);
				smg.setTo(user.getEmail());
				smg.setText(Text);
				smg.setSubject("Account Recovery");
				javamailsender.send(smg);
				flag=0;
				break;
			}
			else
			{
				flag=1;
			}
		}
		if(flag==0)
		{
			return ResponseEntity.ok().body("Verification code sent to your mail");
		}
		else
		{
			return ResponseEntity.ok().body("User Not Found");
		}
	}
	
	public ResponseEntity<String> updatePassword(String passcode,String password)
	{
		int flag=1;
		for(User u:userRepository.findAll())
			{
				if(bs.matches(passcode,u.getPassword()))
				{
					u.setPassword(bs.encode(password));
					userRepository.save(u);
					flag=0;
					break;
				}
				else
				{
					flag=1;
				}
			}
			if(flag==1)
			{
				return ResponseEntity.ok().body("Something Went Wrong, Password Not Changed");
			}
			else
			{
				return ResponseEntity.ok().body("Password Updated");
			}
	}
	
	@Scheduled(cron="0 55 5 28 * ? ")
	public void reportToAccountsDept() throws ResourceNotFoundException
	{
		ByteArrayOutputStream bs=new ByteArrayOutputStream();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Issues");
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
		int rownum = 1;
		Row rowname = sheet.createRow(rownum++);
		int cellnum = 1;
		Cell cellidName = rowname.createCell(cellnum++);
		cellidName.setCellValue("UserId");
		cellidName.setCellStyle(style);
		Cell cellName = rowname.createCell(cellnum++);
		cellName.setCellValue("Name");
		cellName.setCellStyle(style);
		Cell cellUsernameName = rowname.createCell(cellnum++);
		cellUsernameName.setCellValue("UserName");
		cellUsernameName.setCellStyle(style);
		Cell cellEmailName = rowname.createCell(cellnum++);
		cellEmailName.setCellValue("Email");
		cellEmailName.setCellStyle(style);
		Cell cellfineName = rowname.createCell(cellnum++);
		cellfineName.setCellValue("Fine");
		cellfineName.setCellStyle(style);
		for(User us:userRepository.findAll())
		{
			cellnum = 1;
			Row rowvalues = sheet.createRow(rownum++);
			Cell cellid = rowvalues.createCell(cellnum++);
			cellid.setCellValue(us.getId());
			cellid.setCellStyle(styleData);
			Cell cellname = rowvalues.createCell(cellnum++);
			cellname.setCellValue(us.getName());
			cellname.setCellStyle(styleData);
			Cell cellUsername = rowvalues.createCell(cellnum++);
			cellUsername.setCellValue(us.getUsername());
			cellUsername.setCellStyle(styleData);
			Cell cellEmail = rowvalues.createCell(cellnum++);
			cellEmail.setCellValue(us.getEmail());
			cellEmail.setCellStyle(styleData);
			Cell cellfine = rowvalues.createCell(cellnum++);
			cellfine.setCellValue(us.getFine());
			cellfine.setCellStyle(styleData);
		}
		try
		{
			for(int i=0;i<sheet.getPhysicalNumberOfRows();i++)
			{
				Row row =sheet.getRow(i+1);
				for(int j=0;j<row.getPhysicalNumberOfCells();j++)
				{
					sheet.autoSizeColumn(j+1);
				}
			}
			workbook.write(bs);
			MimeMessage msg= javamailsender.createMimeMessage();
			MimeMessageHelper msghelp=new MimeMessageHelper(msg,true);
			msghelp.setFrom(sendermail);
			msghelp.setTo("maheshkambhampati159@gmail.com");
			msghelp.setSubject("Fine Details");
			msghelp.setText("These are the details");
			File excelFile=new File("UsersFine.xlsx");
			FileOutputStream fileout=new FileOutputStream(excelFile);
			fileout.write(bs.toByteArray());
			msghelp.addAttachment("UsersFine.xlsx", excelFile);
			javamailsender.send(msg);
			System.out.println("Mail Sent");
			fileout.close();
			bs.close();
			workbook.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
	

