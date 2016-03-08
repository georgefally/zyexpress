<html>
<head>
    <meta charset="GBK">
    <title>
    姓名身份证导出
    </title>
    <style>
    td{
        mso-number-format:'\@';
    }
    </style>
</head>
<body>
 <table border="1">
    <tr><td>${name!}</td></tr>
    <#list curs as item>
        <tr>
            <td>${item.userName!}</td>
            <td>${item.idNumber!}</td>
        </tr>
    </#list>
 </table>
</body>
</html>