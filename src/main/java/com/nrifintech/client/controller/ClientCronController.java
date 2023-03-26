package com.nrifintech.client.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.Issue;
import com.nrifintech.service.IssueService;

@Controller
@EnableScheduling
public class ClientCronController {

	@Autowired
	private IssueService issueService;

	public boolean issueStillValid(String issueDate) throws ParseException {
		String dateNow = LocalDate.now().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date firstDate = sdf.parse(dateNow);
		Date secondDate = sdf.parse(issueDate);
		long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
		long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		return (diff < 2);
	}

//	@Scheduled(fixedRate = 1000)
	@Scheduled(cron = "0 0 8 1/1 * *")
	public void test() throws ResourceNotFoundException,ParseException {
		List<Issue> issues = issueService.getIssueByStatus("Granted");
		for (Issue issue : issues) {
			if (!issueStillValid(issue.getIssueDate())) {
				issueService.deleteIssue(issue.getIssueId());
			}
		}
	}
}
