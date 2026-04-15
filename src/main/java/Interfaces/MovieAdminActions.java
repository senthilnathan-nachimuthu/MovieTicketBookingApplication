package Interfaces;

import java.util.List;

public interface MovieAdminActions extends MovieCommonActions {
	public boolean CreateMovie(String name, List<String> lang, double  duration);
}