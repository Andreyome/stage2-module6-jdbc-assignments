package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {
    private CustomDataSource dataSource = CustomDataSource.getInstance();

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?,?,?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id =?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers ";

    public Long createUser(User user) {
        Long id = null;
        try(Connection con =CustomDataSource.getInstance().getConnection();
            PreparedStatement prepState = con.prepareStatement(createUserSQL,PreparedStatement.RETURN_GENERATED_KEYS)){
            prepState.setString(1,user.getFirstName());
            prepState.setString(1,user.getLastName());
            prepState.setInt(1,user.getAge());
            prepState.execute();

            ResultSet rs = prepState.getGeneratedKeys();
            if(rs.next()){
                id = rs.getLong(1);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return id;
    }

    public User findUserById(Long userId) {
        User user = null;
        try{
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1,userId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            user = new User (rs.getLong(1),rs.getString(2),rs.getString(3),rs.getInt(4));
        }
        catch(SQLException e){
            //throw new RuntimeException();
            e.printStackTrace();
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;
        try{
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1,userName);
            ResultSet rs = ps.executeQuery();
            rs.next();
            user = new User (rs.getLong(1),rs.getString(2),rs.getString(3),rs.getInt(4));
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> list = new ArrayList<>();
        try{
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findAllUserSQL);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new User(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getInt(4)));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    public User updateUser(User user) {
        try{
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ps.setLong(4,user.getId());
            ps.setString(1,user.getFirstName());
            ps.setString(2,user.getLastName());
            ps.setInt(3,user.getAge());
            ps.executeUpdate();
            return findUserById(user.getId());
        }
        catch(SQLException e){
            throw new RuntimeException();
        }
    }

    private void deleteUser(Long userId) {
        try{
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1,userId);
            ps.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}