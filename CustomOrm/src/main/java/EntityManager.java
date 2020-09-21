import anotations.Column;
import anotations.Entity;
import anotations.Id;
import interfaces.DbContext;

import java.lang.reflect.Field;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EntityManager<E> implements DbContext<E> {

    private final Connection connection;
    public EntityManager(Connection connection){
        this.connection = connection;
    }

    @Override
    public boolean persist(E entity)  {
        Field id = getId(entity.getClass());
        id.setAccessible(true);
        Object value = null;
        try {
            value = id.get(entity);
            if (value==null){
               return   doInsert(entity);

            }else {
               return    doUpdate(entity);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean doUpdate(E entity) {
        try {
            //Update table
            //set  where id = 1
            StringBuilder updateCommand = new StringBuilder();
            updateCommand.append("Update ").append(getTableName(entity)).append(" Set ");
            Field idField = getId(entity.getClass());
            List<Field> fields = Arrays.stream(entity.getClass().getDeclaredFields())
                    .filter(x-> !x.isAnnotationPresent(Id.class))
                    .collect(Collectors.toList());
            for (int i = 0; i < fields.size(); i++) {
                fields.get(i).setAccessible(true);
                String columnName = fields.get(i).getAnnotation(Column.class).name();
                if (fields.get(i).getType() == int.class) {
                    updateCommand.append(columnName).append(" = ").append(fields.get(i).get(entity));
                } else if (fields.get(i).getType() == Date.class) {
                    updateCommand
                            .append(fields.get(i).getAnnotation(Column.class).name())
                            .append(" = ")
                            .append(setDate(entity,fields.get(i)));
                } else {
                    updateCommand
                            .append(fields.get(i).getAnnotation(Column.class).name())
                            .append(" = ")
                            .append(String.format("'%s'", fields.get(i).get(entity)));
                }
                if (i != fields.size() - 1) {
                    updateCommand.append(",");
                }
            }
            idField.setAccessible(true);
            updateCommand.append(" where ")
                    .append(idField.getAnnotation(Column.class).name())
                    .append(" = ")
                    .append(idField.get(entity));
            PreparedStatement ps = connection.prepareStatement(updateCommand.toString());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected!=1){
                throw new SQLException();
            }
            ps.close();
            return true;

        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String setDate(E entity, Field field) throws IllegalAccessException {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String mysqlDateString = formatter.format(field.get(entity));
        return  String.format("'%s'", mysqlDateString);
    }

    private boolean doInsert(E entity) {
        //insert into users(name,password,age,registrationDate)
        //Values('root','1234',1, "2001-09-10")
        StringBuilder insertCommand = new StringBuilder();
        String tableName = getTableName(entity);
        insertCommand.append("Insert into ").append(tableName);
        //columns
        List<Field> fields = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(x-> !x.isAnnotationPresent(Id.class))
                .collect(Collectors.toList());
        insertCommand.append("(");
        insertCommand.append(fields.stream().map(field -> field
                .getAnnotation(Column.class).name())
                .collect(Collectors.joining(","))).append(") VALUES (") ;
        try {
            for (int i = 0; i < fields.size(); i++) {
                fields.get(i).setAccessible(true);
                if (fields.get(i).getType() == int.class) {
                    insertCommand.append(fields.get(i).get(entity));
                } else if (fields.get(i).getType() == Date.class) {
                    insertCommand.append(setDate(entity,fields.get(i)));
                } else {
                    insertCommand.append(String.format("'%s'", fields.get(i).get(entity)));
                }
                if (i != fields.size() - 1) {
                    insertCommand.append(",");
                }
            }
            insertCommand.append(")");
            PreparedStatement ps = connection.prepareStatement(insertCommand.toString(), Statement.RETURN_GENERATED_KEYS);
            int affectedRows = ps.executeUpdate();
            setIdToEntity(affectedRows,ps,entity);
            ps.close();
            return true;
        }
        catch (IllegalAccessException | NullPointerException | SQLException e) {
            e.getMessage();
        }
        return false;
    }

    private void setIdToEntity(int affectedRows,PreparedStatement ps, E entity) throws SQLException, IllegalAccessException {
        if (affectedRows == 0) {
            throw new NullPointerException("Rows affected = 0");
        }
        ResultSet generatedKeys = ps.getGeneratedKeys();

        if (generatedKeys.next()) {
            Field id = getId(entity.getClass());
            id.setAccessible(true);
            id.set(entity, generatedKeys.getInt(1));

        }
    }

    private String getTableName(E entity) {
        return entity.getClass().getAnnotation(Entity.class).name();
    }

    private Field getId(Class<?> entity) {
        return Arrays.stream(entity.getDeclaredFields()).filter(x ->x.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(NullPointerException::new);
    }

    @Override
    public Iterable<E> find(Class<E> table) {
        return null;
    }

    @Override
    public Iterable<E> find(Class<E> table, String where) {
        return null;
    }

    @Override
    public E findFirst(Class<E> table) {
        return null;
    }

    @Override
    public E findFirst(Class<E> table, String where) {
        return null;
    }
}
