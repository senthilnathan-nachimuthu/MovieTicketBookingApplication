package MickeyDBAccess;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;

public class CriteriaBuilder {

	private Criteria criteria;

	public CriteriaBuilder add(String table, String column, Object value, int comparator, Operator operator) {

		if (value == null)
			return this;
		Criteria newCriteria = new Criteria(Column.getColumn(table, column), value, comparator);
		return combine(newCriteria, operator);
	}

	public CriteriaBuilder add(Criteria newCriteria, Operator operator) {

		if (newCriteria == null)
			return this;
		return combine(newCriteria, operator);
	}

	private CriteriaBuilder combine(Criteria newCriteria, Operator operator) {

		if (criteria == null) {
			criteria = newCriteria;
		} else {
			if (operator == Operator.OR) {
				criteria = criteria.or(newCriteria);
			} else {
				criteria = criteria.and(newCriteria);
			}
		}

		return this;
	}

	public Criteria build() {
		return criteria;
	}
}