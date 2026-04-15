package Schemas;

import java.util.List;

public class Movie {
	public long movieId;
	 String movieName;
	 double duration;
	
	 List<String> availableLanguages;
	 List<String> showCreatedLanguaes;
	public List<String> getShowCreatedLanguaes() {
		return showCreatedLanguaes;
	}

	 public void setShowCreatedLanguaes(List<String> showCreatedLanguaes) {
		 this.showCreatedLanguaes = showCreatedLanguaes;
	 }

	public Movie(long movId, String name, List<String> lang, double duration) {
		this.movieName = name.toUpperCase();
		this.duration = duration;
		this.availableLanguages = lang;
		this.movieId = movId;
	}

	public Movie(String movieName2, List<String> movieLanguages, double duration2) {
		
		this.movieName=movieName2;
		this.availableLanguages=movieLanguages;
		this.duration=duration2;
	}

	public List<String> getAvailableLanguages() {
		return availableLanguages;
	}

	public void setAvailableLanguages(List<String> availableLanguages) {
		this.availableLanguages = availableLanguages;
	}

	public long getMovieId() {
		return movieId;
	}

	public void setMovieId(long movieId) {
		this.movieId = movieId;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public String getMovieName() {
		return this.movieName;
	}
	public double getMovieDuration()
	{
		return this.duration;
	}
	
	public void SetMovieName(String newName)
	{
		this.movieName=newName;
		return;
	}
	
	public void setDuration(float duration)
	{
		this.duration=duration;
	}

}
