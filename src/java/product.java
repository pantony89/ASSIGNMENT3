/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Credentials.Credentials;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author c0633176
 */
@WebServlet(urlPatterns = {"/products"})
public class product extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private String getResults(String query, String... params) {
        JSONArray jArray = new JSONArray();
        StringBuilder sb = new StringBuilder();
        Boolean isSingle = false;
        try (Connection cn = Credentials.getConnection()) {
            PreparedStatement pstmt = cn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
                isSingle = true;
            }
            ResultSet rs = pstmt.executeQuery();
            if (isSingle == false) {
                while (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("productId", rs.getInt("productId"));
                    json.put("name", rs.getString("name"));
                    json.put("description", rs.getString("description"));
                    json.put("quantity", rs.getInt("quantity"));
                    jArray.add(json);
                }
            } else {
                while (rs.next()) {
                    JsonObject jsonObj = Json.createObjectBuilder()
                            .add("productId", rs.getInt("productId"))
                            .add("name", rs.getString("name"))
                            .add("description", rs.getString("description"))
                            .add("quantity", rs.getInt("quantity"))
                            .build();
                    return jsonObj.toString();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(product.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jArray.toJSONString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Content-Type", "text/plain-text");
        try (PrintWriter out = response.getWriter()) {
            if (!request.getParameterNames().hasMoreElements()) {
                out.println(getResults("SELECT * FROM PRODUCT"));
            } else {
                int id = Integer.parseInt(request.getParameter("id"));
                out.println(getResults("SELECT * FROM PRODUCT WHERE productId = ?", String.valueOf(id)));
            }
        } catch (IOException ex) {
            Logger.getLogger(product.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("id") && keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
                String id = request.getParameter("id");
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                doUpdate("INSERT INTO PRODUCT (productId, name, description, quantity) VALUES (?, ?, ?, ?)", id, name, description, quantity);
            } else {
                out.println("Not enough arguments to peform this action");
            }
        } catch (IOException ex) {
            Logger.getLogger(product.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int doUpdate(String query, String... params) {
        int changes = 0;
        try (Connection cn = Credentials.getConnection()) {
            PreparedStatement pstmt = cn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            changes = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(product.class.getName()).log(Level.SEVERE, null, ex);
        }
        return changes;
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("id")) {
                String id = request.getParameter("id");
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                doUpdate("UPDATE PRODUCT SET productId = ?, name = ?, description = ?, quantity = ? WHERE productId = ?", id, name, description, quantity, id);
            } else {
                out.println("Not enough arguments to peform this action");
            }
        } catch (IOException ex) {
            Logger.getLogger(product.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("id")) {
                String id = request.getParameter("id");
                doUpdate("DELETE FROM PRODUCT WHERE productId = ?", id);
            } else {
                out.println("Not enough arguments to peform this action");
            }
        } catch (IOException ex) {
            Logger.getLogger(product.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
