package DBConnectivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

public class connectDB {

	public Connection getConnection() {
		String url = "jdbc:mysql://localhost:3306/movie_ticket_booking_db";
		String username = "root";
		String password = "root321";
		Connection connection = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, username, password);
			return connection;

		} catch (Exception e) {
			System.out.println(e);
		}
		return connection;
	}

	public long getGeneratedKey(PreparedStatement preStatement) {
		try (ResultSet rs = preStatement.getGeneratedKeys()) {
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}

	public int updateData(String query, Object... params) {
		Connection connection = getConnection();
		if (connection == null) {
			System.out.println("Conection Failed..");
			return -1;
		}
		try {
			PreparedStatement preStatement = getPreparedStatement(connection, query, params);
			if (preStatement != null) {
				int row = preStatement.executeUpdate();
				return row;
			}
		} catch (SQLException e) {
			System.out.println("Error While Updating Data" + e);
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return 0;
	}

	public long insertData(String query, Object... params) {
		Connection connection = getConnection();
		if (connection == null) {
			System.out.println("Conection Failed..");
			return -1;
		}
		try {
			PreparedStatement preStatement = getPreparedStatement(connection, query, params);
			if (preStatement != null) {
				int row = preStatement.executeUpdate();
				if (row > 0) {
					return getGeneratedKey(preStatement);
				}
			}
		} catch (SQLException e) {
			System.out.println("Error While Inserting Data" + e);
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return -1;
	}

	public CachedRowSet getData(String query, Object... params) {
		Connection connection = getConnection();
		if (connection == null) {
			System.out.println("Conection Failed..");
			return null;
		}
		try {
			PreparedStatement preStatement = getPreparedStatement(connection, query, params);
			if (preStatement != null) {
				CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
				ResultSet rs = preStatement.executeQuery();
				crs.populate(rs);
				return crs;
			}

		} catch (Exception e) {
			System.out.println(e);
		} finally {

			closeConnection(connection);
		}
		return null;
	}

	public int executeTransaction(List<QueryHelper> list) throws SQLException {
		Connection connection = getConnection();
		if (connection == null) {
			System.out.println("Conection Failed..");
			return -1;
		}
		try {
			connection.setAutoCommit(false);
			for (QueryHelper qh : list) {
				PreparedStatement pstm = getPreparedStatement(connection, qh.getQuery(), qh.getParams());
				pstm.executeUpdate();
				pstm.close();

			}
			connection.commit();

		} catch (Exception e) {
			if (connection != null)
				connection.rollback();
			throw e;
		} finally {

			closeConnection(connection);
		}
		return 0;

	}

	public List<Long> executeBatchQuery(String query, List<Object[]> objectList) {
		Connection connection = getConnection();
		try {
			connection.setAutoCommit(false);
			PreparedStatement pstm = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < objectList.size(); i++) {
				Object[] params = objectList.get(i);
				for (int j = 1; j <= params.length; j++) {
					pstm.setObject(j, params[j - 1]);
				}
				pstm.addBatch();
			}
			if (pstm != null) {
				
				pstm.executeBatch();
				ResultSet rs = pstm.getGeneratedKeys();
				List<Long> IdList = new ArrayList<>();
				while (rs.next()) {
					IdList.add(rs.getLong(1));
				}
				connection.commit();
				return IdList;
			}

		} catch (Exception e) {
			if(connection!=null)
			{
				try {
					connection.rollback();
					throw e;
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		finally {
			closeConnection(connection);
		}
		return null;
	}

	public PreparedStatement getPreparedStatement(Connection connection, String query, Object[] params) {
		try {
			PreparedStatement preStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			for (int i = 1; i <= params.length; i++) {
				preStatement.setObject(i, params[i - 1]);
			}
			return preStatement;

		} catch (Exception e) {
			System.out.println("Error while Preparing statement." + e);
		}
		return null;
	}

	private void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.out.println("Error while closing Connection " + e);
			}
		}

	}

}
