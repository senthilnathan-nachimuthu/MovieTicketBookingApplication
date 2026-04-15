package Interfaces;

import java.util.List;

import Schemas.Screen;
import Schemas.Show;

public interface TheatreUserActions extends TheatreCommonActions {

	public double getTotalFair(Show show, List<Long> seats, Screen screenObj);

}
