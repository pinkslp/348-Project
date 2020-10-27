<!DOCTYPE html>
<html>
  <head>
    <title>The App Engine Blog</title>
    
  </head>
  <body>
<div>
  <c:choose>
    <c:when test="${id == null}">
      <h2>Create a new blog post</h2>
      <form method="POST" action="HelloAppEngine">
    </c:when>
    <c:otherwise>
      <h2><c:out value="${pagetitle}" /></h2>
      <form method="POST" action="/update">
      <input type="hidden" name="blogContent_id" value="${id}">
    </c:otherwise>
  </c:choose>

    <div>
      <label for="name">Name</label>
      <input type="text" name="person_name" id="name" size="40" value="${name}" />
    </div>

    <div>
      <label for="p_id">p_id</label>
      <input type="int" name="person_p_id" id="p_id" size="40" value="${author}" />
    </div>

    <div>
      <label for="vehicle">Vehicle preference</label>
      <input type = "text" name="person_vehicle" id="vehicle" size="40" value="${vehicle}" />
    </div>

    <div>
      <label for="dob">Date of Birth</label>
      <input type = "date" name="person_dob" id="dob" />
    </div>

    <button type="submit">Save</button>
  </form>
</div>

  </body>
</html>