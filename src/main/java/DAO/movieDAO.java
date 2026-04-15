package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.movieticketbooking.*;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.Row;

import DBConnectivity.connectDB;
import Exceptions.ApplicationException;
import MickeyDBAccess.CRUDOperation;
import MickeyDBAccess.CRUDUtil;
import MickeyDBAccess.CriteriaBuilder;
import MickeyDBAccess.Operator;
import Schemas.Movie;

public class movieDAO {

	connectDB connectDbObj = new connectDB();

	public long insertMovie(String name, List<String> lang, double duration) {

		Row row = new Row(MOVIE.TABLE);
		row.set(MOVIE.MOVIE_NAME, name);
		row.set(MOVIE.DURATION, duration);
		DataObject dob = CRUDOperation.insertOneRow(MOVIE.TABLE, row);
		if (CRUDUtil.DOBValidator(dob)) {
			Row insertResult = CRUDUtil.getFirstRow(dob, MOVIE.TABLE);
			if (insertResult != null) {
				long movieId = CRUDUtil.parseLongFromRow(insertResult, MOVIE.MOVIE_ID);
				LanguageMappingFunction(movieId, lang);
				return movieId;
			}
		}
		return -1;
	}

	public Map<String, Long> getAllLanguages() {

		Map<String, Long> languageTemp = new HashMap<>();
		DataObject dob = CRUDOperation.SelectAllColumns(MOVIE_LANGUAGE.TABLE, null, null);
		if (CRUDUtil.DOBValidator(dob)) {
			languageTemp = getLanguageMapping(dob);
			return languageTemp;
		}
		return languageTemp;
	}

	private Map<String, Long> getLanguageMapping(DataObject dob) {
		Map<String, Long> languageTemp = new HashMap<>();

		try {
			Iterator<Row> rows = dob.getRows(MOVIE_LANGUAGE.TABLE);
			while (rows.hasNext()) {
				Row langRow = rows.next();
				if (langRow != null) {
					long language_id = CRUDUtil.parseLongFromRow(langRow, MOVIE_LANGUAGE.LANGUAGE_ID);
					String language = CRUDUtil.parseStringFromRow(langRow, MOVIE_LANGUAGE.LANGUAGE);
					languageTemp.put(language, language_id);
				}
			}

		} catch (DataAccessException e) {

			throw new RuntimeException("Exception occurs during Movie Language Mapping", e);
		}

		return languageTemp;
	}

	public boolean restoreMovie(long movie_id) {

		Criteria criteria = constructMovieIdCriteria(movie_id);
		DataObject dob = CRUDOperation.updateSingleColumn(MOVIE.TABLE, criteria, MOVIE.IS_DELETED, false);
		return CRUDUtil.DOBValidator(dob);
	}

	public boolean isMovieDeleted(long movie_id) {

		Criteria criteria = constructMovieIdCriteria(movie_id);
		DataObject dob = CRUDOperation.SelectSpecificColumns(MOVIE.TABLE, criteria,
				new Column[] { Column.getColumn(MOVIE.TABLE, MOVIE.IS_DELETED) });
		if (CRUDUtil.DOBValidator(dob)) {
			Row row = CRUDUtil.getFirstRow(dob, MOVIE.TABLE);
			if (row != null) {
				return (boolean) row.get(MOVIE.IS_DELETED);
			}
		}
		throw new ApplicationException("Movie Not Found");
	}

	public boolean isMovieLanguageExists(long movie_id, String lang) {

		Criteria criteria = constructMovieIdCriteria(movie_id);
		DataObject dob = CRUDOperation.SelectSpecificColumns(MOVIE_LANGUAGE_MAPPING.TABLE, criteria,
				new Column[] { Column.getColumn(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE_MAPPING.LANGUAGE_ID) });

		if (CRUDUtil.DOBValidator(dob)) {
			try {
				Iterator<Row> rows = dob.getRows(MOVIE_LANGUAGE_MAPPING.TABLE);
				while (rows.hasNext()) {
					Row row = rows.next();
					String lang2 = findLanguageById(CRUDUtil.parseLongFromRow(row, MOVIE_LANGUAGE_MAPPING.LANGUAGE_ID));
					if (lang2.equals(lang)) {
						return true;
					}
				}
			} catch (DataAccessException e) {

				throw new RuntimeException("Exception Occurs while Checking Movie Already Exists or Not", e);
			}

		}
		return false;
	}

	private String findLanguageById(long lang_id) {

		Criteria criteria = new CriteriaBuilder()
				.add(MOVIE_LANGUAGE.TABLE, MOVIE_LANGUAGE.LANGUAGE_ID, lang_id, QueryConstants.EQUAL, null).build();
		DataObject dob = CRUDOperation.SelectAllColumns(MOVIE_LANGUAGE.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			Row row = CRUDUtil.getFirstRow(dob, MOVIE_LANGUAGE.TABLE);
			return CRUDUtil.parseStringFromRow(row, MOVIE_LANGUAGE.LANGUAGE);
		}
		return null;
	}

	public void LanguageMappingFunction(long mid, List<String> lang) {
		Map<String, Long> availableLanguages = getAllLanguages();
		for (String language : lang) {

			if (availableLanguages.get(language) == null) {
				long language_id = insertLanguage(language);
				if (language_id != -1) {
					insertMovieLanguageMapping(mid, language_id);
				}
			} else {
				long language_id = availableLanguages.get(language);
				insertMovieLanguageMapping(mid, language_id);
			}
		}
	}

	public void insertMovieLanguageMapping(long movieId, long languageId) {

		Row row = new Row(MOVIE_LANGUAGE_MAPPING.TABLE);
		row.set(MOVIE_LANGUAGE_MAPPING.MOVIE_ID, movieId);
		row.set(MOVIE_LANGUAGE_MAPPING.LANGUAGE_ID, languageId);
		DataObject dob = CRUDOperation.insertOneRow(MOVIE_LANGUAGE_MAPPING.TABLE, row);
	}

	public long insertLanguage(String lang) {

		Row row = new Row(MOVIE_LANGUAGE.TABLE);
		row.set(MOVIE_LANGUAGE.LANGUAGE, lang);
		DataObject dob = CRUDOperation.insertOneRow(MOVIE_LANGUAGE.TABLE, row);
		if (CRUDUtil.DOBValidator(dob)) {
			Row insertedRow = CRUDUtil.getFirstRow(dob, MOVIE_LANGUAGE.TABLE);
			if (insertedRow != null) {
				return CRUDUtil.parseLongFromRow(insertedRow, MOVIE_LANGUAGE.LANGUAGE_ID);
			}
		}
		return -1;
	}

	public long findLanguage(String lang) {
		Criteria criteria = new CriteriaBuilder()
				.add(MOVIE_LANGUAGE.TABLE, MOVIE_LANGUAGE.LANGUAGE, lang, QueryConstants.EQUAL, null).build();
		DataObject dob = CRUDOperation.SelectAllColumns(MOVIE_LANGUAGE.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			Row row = CRUDUtil.getFirstRow(dob, MOVIE_LANGUAGE.TABLE);
			return CRUDUtil.parseLongFromRow(row, MOVIE_LANGUAGE.LANGUAGE_ID);
		}
		return -1;
	}

	public Movie isMovieExists(String movie_name, double duration) {

		Criteria criteria = new CriteriaBuilder()
				.add(MOVIE.TABLE, MOVIE.MOVIE_NAME, movie_name, QueryConstants.EQUAL, null)
				.add(MOVIE.TABLE, MOVIE.DURATION, duration, QueryConstants.EQUAL, Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(MOVIE.TABLE), Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumns(MOVIE.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			Row row;
			try {
				row = dob.getFirstRow(MOVIE.TABLE);
				long movie_id = CRUDUtil.parseLongFromRow(row, MOVIE.MOVIE_ID);
				double duration2 = CRUDUtil.parseDoubleFromRow(row, MOVIE.DURATION);
				if (movie_id != 0 && duration == duration2) {
					return findMovieById(movie_id);
				}
			} catch (DataAccessException e) {
				throw new RuntimeException(e);
			}
		}

		return null;
	}

	public Movie findMovieByName(String name) {

		Criteria criteria = new CriteriaBuilder()
				.add(MOVIE.TABLE, MOVIE.MOVIE_NAME, name, QueryConstants.EQUAL, Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(MOVIE.TABLE), Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumns(MOVIE.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getMovieObject(dob);
		}
		return null;
	}

	public List<String> findMovieLanguage(long movie_id) {

		Join j1 = CRUDUtil.buildJoin(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE.TABLE,
				new String[] { MOVIE_LANGUAGE_MAPPING.LANGUAGE_ID }, new String[] { MOVIE_LANGUAGE.LANGUAGE_ID },
				Join.INNER_JOIN);

		Criteria criteria = new CriteriaBuilder().add(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE_MAPPING.MOVIE_ID,
				movie_id, QueryConstants.EQUAL, Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(MOVIE_LANGUAGE_MAPPING.TABLE, criteria,
				Arrays.asList(j1), null);
		List<String> languageList = new ArrayList<>();
		if (CRUDUtil.DOBValidator(dob)) {
			Iterator<Row> rows;
			try {
				rows = dob.getRows(MOVIE_LANGUAGE.TABLE);
				while (rows.hasNext()) {
					Row row = rows.next();
					languageList.add(CRUDUtil.parseStringFromRow(row, MOVIE_LANGUAGE.LANGUAGE));
				}
			} catch (DataAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return languageList;

	}

	public List<Movie> findAllMovies() {

		Criteria criteria = CRUDUtil.getNotDeletedCriteria(MOVIE.TABLE);
		DataObject dob = CRUDOperation.SelectAllColumns(MOVIE.TABLE, criteria, null);
		return getMovieObjectList(dob);
	}

	public Map<Long, Movie> findAllMovieByMapping() {
		Criteria criteria = CRUDUtil.getNotDeletedCriteria(MOVIE.TABLE);
		DataObject dob = CRUDOperation.SelectAllColumns(MOVIE.TABLE, criteria, null);
		return getMovieObjectListByMapping(dob);
	}

	public boolean deleteMovie(long movieId) {

		Criteria criteria = new CriteriaBuilder().add(MOVIE.TABLE, MOVIE.MOVIE_ID, movieId, QueryConstants.EQUAL, null)
				.build();

		DataObject dob = CRUDOperation.updateSingleColumn(MOVIE.TABLE, criteria, MOVIE.IS_DELETED, true);
		return (CRUDUtil.DOBValidator(dob));
	}

	public boolean updateMovieName(long movieId, String movieName) {

		Movie movieObj = findMovieById(movieId);
		if (isMovieExists(movieName, movieObj.getMovieDuration()) != null) {

			System.out.println("Cannot update movie name, already this movie with same duration found.");
			return false;
		}

		Criteria criteria = new CriteriaBuilder().add(MOVIE.TABLE, MOVIE.MOVIE_ID, movieId, QueryConstants.EQUAL, null)
				.add(CRUDUtil.getNotDeletedCriteria(MOVIE.TABLE), null).build();
		DataObject dob = CRUDOperation.updateSingleColumn(MOVIE.TABLE, criteria, MOVIE.MOVIE_NAME, movieName);
		return (CRUDUtil.DOBValidator(dob));
	}

	public void updateMovieDetails(Movie movieObj, List<String> newLanguages) {

		Criteria criteria = new CriteriaBuilder()
				.add(MOVIE.TABLE, MOVIE.MOVIE_ID, movieObj.getMovieId(), QueryConstants.EQUAL, null)
				.add(CRUDUtil.getNotDeletedCriteria(MOVIE.TABLE), null).build();

		DataObject dob = CRUDOperation.updateMultipleColumns(MOVIE.TABLE,
				new String[] { MOVIE.MOVIE_NAME, MOVIE.DURATION },
				new Object[] { movieObj.getMovieName(), movieObj.getDuration() }, criteria);

		deleteShowMappings(movieObj.movieId);
		deleteMappings(movieObj.getMovieId());
		LanguageMappingFunction(movieObj.getMovieId(), newLanguages);

	}

	private void deleteShowMappings(long movieId) {
		Criteria criteria = new Criteria(
				Column.getColumn(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE_MAPPING.MOVIE_ID), movieId,
				QueryConstants.EQUAL);
		DataObject dob = CRUDOperation.SelectAllColumns(MOVIE_LANGUAGE_MAPPING.TABLE, criteria, null);
		try {
			Iterator<Row> rows = dob.getRows(MOVIE_LANGUAGE_MAPPING.TABLE);
			Criteria newCriteria = null;
			while (rows.hasNext()) {
				Row row = rows.next();
				long mapping_id = CRUDUtil.parseLongFromRow(row, MOVIE_LANGUAGE_MAPPING.LANGUAGE_MAPPING_ID);
				if (newCriteria == null) {
					newCriteria = new Criteria(
							Column.getColumn(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.LANGUAGE_MAPPING_ID),
							mapping_id, QueryConstants.EQUAL);
				} else {
					newCriteria=newCriteria.or(new Criteria(
							Column.getColumn(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.LANGUAGE_MAPPING_ID),
							mapping_id, QueryConstants.EQUAL));
				}
				
			}
			CRUDOperation.deleteRows(THEATRE_MOVIE_DETAILS.TABLE, newCriteria);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void deleteMappings(long movieId) {

		try {
			Criteria criteria = new Criteria(
					Column.getColumn(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE_MAPPING.MOVIE_ID), movieId,
					QueryConstants.EQUAL);
			CRUDOperation.deleteRows(MOVIE_LANGUAGE_MAPPING.TABLE, criteria);
		} catch (Exception e) {
			throw new RuntimeException("Error deleting movie language mappings", e);
		}
	}

	public long findMovieMappingId(long movieId, long language_id) {

		Criteria criteria = new CriteriaBuilder()
				.add(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE_MAPPING.MOVIE_ID, movieId, QueryConstants.EQUAL,
						Operator.AND)
				.add(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE_MAPPING.LANGUAGE_ID, language_id,
						QueryConstants.EQUAL, Operator.AND)
				.build();

		DataObject dob = CRUDOperation.SelectAllColumns(MOVIE_LANGUAGE_MAPPING.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			Row row = CRUDUtil.getFirstRow(dob, MOVIE_LANGUAGE_MAPPING.TABLE);
			if (row != null) {
				return CRUDUtil.parseLongFromRow(row, MOVIE_LANGUAGE_MAPPING.LANGUAGE_MAPPING_ID);
			}
		}
		return -1;

	}

	public Movie findMovieById(long movieId) {

		Criteria criteria = new CriteriaBuilder()
				.add(MOVIE.TABLE, MOVIE.MOVIE_ID, movieId, QueryConstants.EQUAL, Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(MOVIE.TABLE), Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumns(MOVIE.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getMovieObject(dob);
		}
		return null;

	}

	public List<Movie> getMovieObjectList(DataObject dob) {
		List<Movie> list = new ArrayList<>();

		try {

			Iterator<Row> rows = dob.getRows(MOVIE.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long movie_id = CRUDUtil.parseLongFromRow(row, MOVIE.MOVIE_ID);
				String movie_name = CRUDUtil.parseStringFromRow(row, MOVIE.MOVIE_NAME);
				double duration = CRUDUtil.parseDoubleFromRow(row, MOVIE.DURATION);
				List<String> lang = findMovieLanguage(movie_id);

				if (movie_id != 0 && lang != null) {
					Movie movieObj = new Movie(movie_id, movie_name, lang, duration);
					list.add(movieObj);
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;

	}

	private Map<Long, Movie> getMovieObjectListByMapping(DataObject dob) {
		Map<Long, Movie> list = new HashMap<>();
		try {

			Iterator<Row> rows = dob.getRows(MOVIE.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long movie_id = CRUDUtil.parseLongFromRow(row, MOVIE.MOVIE_ID);
				String movie_name = CRUDUtil.parseStringFromRow(row, MOVIE.MOVIE_NAME);
				double duration = CRUDUtil.parseDoubleFromRow(row, MOVIE.DURATION);
				List<String> lang = findMovieLanguage(movie_id);

				if (movie_id != 0 && lang != null) {
					Movie movieObj = new Movie(movie_id, movie_name, lang, duration);
					list.put(movie_id, movieObj);
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	public Movie getMovieObject(DataObject dob) {
		try {

			Iterator<Row> rows = dob.getRows(MOVIE.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long movie_id = CRUDUtil.parseLongFromRow(row, MOVIE.MOVIE_ID);
				String movie_name = CRUDUtil.parseStringFromRow(row, MOVIE.MOVIE_NAME);
				double duration = CRUDUtil.parseDoubleFromRow(row, MOVIE.DURATION);
				List<String> lang = findMovieLanguage(movie_id);

				if (movie_id != 0 && lang != null) {
					return new Movie(movie_id, movie_name, lang, duration);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public long getFirstResultValue(ResultSet rs) {
		try {
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return -1;
	}

	private Criteria constructMovieIdCriteria(long movieId) {
		Criteria criteria = new CriteriaBuilder().add(MOVIE.TABLE, MOVIE.MOVIE_ID, movieId, QueryConstants.EQUAL, null)
				.build();
		return criteria;
	}

}
