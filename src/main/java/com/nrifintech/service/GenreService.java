package com.nrifintech.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nrifintech.model.Genre;
import com.nrifintech.repository.GenreRepository;

@Service
public class GenreService 
{
	@Autowired
	private GenreRepository genrerepo;

	public List<Genre> showall()
	{
		List<Genre> glist=new ArrayList<Genre>();
		for(Genre g:genrerepo.findAll())
		{
			glist.add(g);
		}
		return glist;
	}
	
	public String getGenrename(int genreId)
	{
		for(Genre g:genrerepo.findAll())
		{
			if(g.getGenreId()==genreId)
			{
				return g.getGenreName();
			}
		}
		return null;
	}
	
	public int getGenreId(String genreName)
	{
		int flag=0;
		for(Genre g:genrerepo.findAll())
		{
			if(g.getGenreName().equalsIgnoreCase(genreName))
			{
				flag=g.getGenreId();
			}
		}
		
		if(flag==0)
		{
			return 0;
		}
		else
		{
			return flag;
		}
	}

}
