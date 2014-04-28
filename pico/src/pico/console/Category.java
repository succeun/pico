package pico.console;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import pico.LinkUtil;

/**
 * 트리구조의 메뉴를 만들기 위한, 각각의 메뉴를 나타내는 클래스이다.
 * @author Eun Jeong-Ho, silver@intos.biz
 * @version 2007. 1. 25
 */
@SuppressWarnings("serial")
public class Category implements Serializable
{
    public static final int UNKNOWN_ID = -1;
    private int id = UNKNOWN_ID;
    private String name;
    private Class<?> controller;
    private String method;
    private String url;
    private Category parent;

    private List<Category> categories = new ArrayList<Category>();
    private boolean select; // 선택여부
    private boolean isVisablity = true;
    private boolean isPopup; // 새로운 창으로 링크를 띄울지 여부

    public Category(HttpServletRequest req)
    {
        this(req, null, 0, "", null, null, null, null);
    }

    public Category(HttpServletRequest req, int id)
    {
        this(req, null, id, "", null, null, null, null);
    }

    public Category(HttpServletRequest req, int id, String name)
    {
        this(req, null, id, name, null, null, null, null);
    }

    /**
     * 메뉴를 만든다.
     * @param parent 부모 메뉴
     * @param name 화면상의 이름
     */
    public Category(HttpServletRequest req, Category parent, int id, String name)
    {
        this(req, parent, id, name, null, null, null, null);
    }

    /**
     * 메뉴를 만든다.
     * URL은 콘트롤과 메소드명으로 만들어낸다.
     * @param parent 부모 메뉴
     * @param name 화면상의 이름
     * @param controller 콘트롤러 Class
     * @param method 메소드명
     */
    public Category(HttpServletRequest req, Category parent, int id, String name, Class<?> controller, String method)
    {
        this(req, parent, id, name, controller, method, null, null);
    }

    /**
     * 메뉴를 만든다.
     * URL은 콘트롤과 메소드명으로 만들어낸다.
     * @param parent 부모 메뉴
     * @param name 화면상의 이름
     * @param controller 콘트롤러 Class
     * @param method 메소드명
     * @param urlParam 특정 URL Parameter
     */
    public Category(HttpServletRequest req, Category parent, int id, String name, Class<?> controller, String method, String urlParam)
    {
        this(req, parent, id, name, controller, method, null, urlParam);
    }

    /**
     * 메뉴를 만든다.
     * @param parent 부모 메뉴
     * @param name 화면상의 이름
     * @param url 특정 URL
     */
    public Category(HttpServletRequest req, Category parent, int id, String name, String url)
    {
        this(req, parent, id, name, null, null, url, null);
    }

    /**
     * 메뉴를 만든다.
     * @param parent 부모 메뉴
     * @param name 화면상의 이름
     * @param url 특정 URL
     * @param urlParam 특정 URL Parameter
     */
    public Category(HttpServletRequest req, Category parent, int id, String name, String url, String urlParam)
    {
        this(req, parent, id, name, null, null, url, urlParam);
    }

    /**
     * 메뉴를 만든다.
     * @param parent 부모 메뉴
     * @param name 화면상의 이름
     */
    public Category(HttpServletRequest req, Category parent, String name)
    {
        this(req, parent, UNKNOWN_ID, name, null, null, null, null);
    }

    /**
     * 메뉴를 만든다.
     * URL은 콘트롤과 메소드명으로 만들어낸다.
     * @param parent 부모 메뉴
     * @param name 화면상의 이름
     * @param controller 콘트롤러 Class
     * @param method 메소드명
     */
    public Category(HttpServletRequest req, Category parent, String name, Class<?> controller, String method)
    {
        this(req, parent, UNKNOWN_ID, name, controller, method, null, null);
    }

    /**
     * 메뉴를 만든다.
     * URL은 콘트롤과 메소드명으로 만들어낸다.
     * @param parent 부모 메뉴
     * @param name 화면상의 이름
     * @param controller 콘트롤러 Class
     * @param method 메소드명
     * @param urlParam 특정 URL Parameter
     */
    public Category(HttpServletRequest req, Category parent, String name, Class<?> controller, String method, String urlParam)
    {
        this(req, parent, UNKNOWN_ID, name, controller, method, null, urlParam);
    }

    /**
     * 메뉴를 만든다.
     * @param parent 부모 메뉴
     * @param name 화면상의 이름
     * @param url 특정 URL
     */
    public Category(HttpServletRequest req, Category parent, String name, String url)
    {
        this(req, parent, UNKNOWN_ID, name, null, null, url, null);
    }

    /**
     * 메뉴를 만든다.
     * @param parent 부모 메뉴
     * @param name 화면상의 이름
     * @param url 특정 URL
     * @param urlParam 특정 URL Parameter
     */
    public Category(HttpServletRequest req, Category parent, String name, String url, String urlParam)
    {
        this(req, parent, UNKNOWN_ID, name, null, null, url, urlParam);
    }

    /**
     * 메뉴를 만든다.
     * @param parent 부모 메뉴
     * @param id id
     * @param name 화면상의 이름
     * @param controller 콘트롤러 Class
     * @param method 메소드명
     * @param url 특정 URL
     * @param urlParam 특정 URL Parameter
     */
    private Category(HttpServletRequest req, Category parent, int id, String name, Class<?> controller, String method, String url, String urlParam)
    {
        this.id = id;
        this.name = name;

        if (controller != null && method != null)
        {
            this.controller = controller;
            this.method = method;
            this.url = LinkUtil.toLink(req, controller, method);
        }
        else if (url != null)
            this.url = url;

        if (urlParam != null)
            this.url += "?" + urlParam;

        setParent(parent);
    }

    public Category getParent()
    {
        return parent;
    }

    public void setParent(Category parent)
    {
        if (parent != null)
        {
            this.parent = parent;
            this.parent.addChild(this);
        }
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Class<?> getController()
    {
        return controller;
    }

    public void setController(Class<?> controller)
    {
        this.controller = controller;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public boolean isPopup()
    {
        return this.isPopup;
    }

    public void setPopup(boolean isNewOpen)
    {
        this.isPopup = isNewOpen;
    }

    /**
     * 자식 메뉴들을 반환한다.
     * @return List Category List
     */
    public List<Category> getChildren()
    {
        return categories;
    }

    public Category getChild(String name)
    {
        for (int i = 0; i < categories.size();i++)
        {
            Category c = categories.get(i);
            if (name != null && name.equals(c.getName()))
                return c;
        }
        return null;
    }

    public Category getChild(int id)
    {
        for (int i = 0; i < categories.size();i++)
        {
            Category c = (Category)categories.get(i);
            if (id == c.getId())
                return c;
        }
        return null;
    }

    public void addChild(Category category)
    {
        this.categories.add(category);
    }

    /**
     * 보여줄지 여부를 결정한다.
     * @return 보여줄지 여부
     */
    public boolean isVisablity()
    {
        return isVisablity;
    }

    public void setVisablity(boolean visablity)
    {
        isVisablity = visablity;
    }

    /**
     * 선택된 메뉴인지 여부를 반환한다.
     * @return 선택여부
     */
    public boolean isSelect()
    {
        return this.select;
    }

    /**
     * 메뉴를 선택처리하여, 부모메뉴라인까지 처리한다.
     */
    public void setSelect()
    {
        this.select = true;
        if (parent != null)
            this.parent.setSelect();
    }

    /**
     * 현재 메뉴가 루트로부터의 패스를 얻는다.
     * @return 패스
     */
    public String getPath()
    {
        return getPath(false);
    }

    /**
     * 현재 메뉴가 루트로부터의 패스를 얻는다.
     * @param isContainLink link를 포함할지 여부
     * @return 패스
     */
    public String getPath(boolean isContainLink)
    {
        String path = (this.name != null) ? this.name : "";
        Category pt = this.parent;
        while (pt != null)
        {
            if (pt.getName() != null && pt.getName().length() > 0)
            {
                String url = pt.getUrl();
                if (isContainLink && url != null && url.length() > 0)
                    path =  "<a href='" + pt.getUrl() +"'>" + pt.getName() + "</a> &gt; " + path;
                else
                    path =  pt.getName() + " &gt; " + path;
            }
            pt = pt.parent;
        }

        return path;
    }

    /**
     * 선택된 메뉴의 패스를 루트부터 검사하여 반환한다.
     * 단, 선택이 이루어 져야 한다.
     * @return 선택된 메뉴의 패스
     */
    public String getSelectPath()
    {
        Category current = getRoot();
        while (current.isSelect())
        {
            List<Category> list = current.categories;
            if (list.size() == 0)
                break;

            for (int i = 0; i < list.size();i++)
            {
                Category submenu = (Category)list.get(i);
                if (submenu.isSelect())
                {
                    current = submenu;
                    break;
                }
            }
        }

        return current.getPath();
    }

    /**
     * 메뉴의 가장 최상의 Root 메뉴를 반환한다.
     * @return  루트 메뉴
     */
    public Category getRoot()
    {
        Category root = (this.parent == null) ? this : this.parent;
        Category tmp = root;
        while (tmp != null)
        {
            root = tmp;
            tmp = root.parent;
        }
        return root;
    }

    /**
     * 현재 하위로 선택된 메뉴인지를 검사하여 선택처리하고 해당 Menu를 반환한다.
     * @param classFullName 클래스명
     * @param method 명
     * @return 선택된 메뉴, 선택된 것이 없다면, null을 반환한다.
     */
    public Category getByController(String classFullName, String method)
    {
        if (classFullName != null && this.controller != null && method != null && this.method != null &&
                classFullName.equals(this.controller.getName()) && method.equals(this.method))
        {
            return this;
        }
        else
        {
            for (int i = 0; i < categories.size();i++)
                ((Category) categories.get(i)).getByController(classFullName, method);
        }

        return null;
    }

    /**
     * 현재 하위로 선택된 메뉴인지를 검사하여 선택처리하고 해당 Menu를 반환한다.
     * @param url URL, 메뉴에 설정된 URL을 검사하여 선택처리한다.
     * @return 선택된 메뉴, 선택된 것이 없다면, null을 반환한다.
     */
    public Category getByURL(String url)
    {
        Category selectCategory = null;
        if (url != null && this.url != null && url.startsWith(this.url))
        {
            selectCategory = this;
        }
        else
        {
            for (int i = 0; i < categories.size();i++)
            {
                selectCategory = ((Category) categories.get(i)).getByURL(url);
                if (selectCategory != null)
                    break;
            }
        }

        return selectCategory;
    }

    /**
     * 현재 하위로 선택된 메뉴인지를 검사하여 선택처리하고 해당 Menu를 반환한다.
     * @param id 아이디
     * @return 선택된 메뉴, 선택된 것이 없다면, null을 반환한다.
     */
    public Category getById(int id)
    {
        Category selectCategory = null;
        if (id == this.id)
        {
            selectCategory = this;
        }
        else
        {
            for (int i = 0; i < categories.size();i++)
            {
                selectCategory = ((Category) categories.get(i)).getById(id);
                if (selectCategory != null)
                    break;
            }
        }

        return selectCategory;
    }

    /**
     * 현재 하위로 선택된 메뉴인지를 검사하여 선택처리하고 해당 Menu를 반환한다.
     * @param name 이름
     * @return 선택된 메뉴, 선택된 것이 없다면, null을 반환한다.
     */
    public Category getByName(String name)
    {
        Category selectCategory = null;
        if (name != null && name.equals(this.name))
        {
            selectCategory = this;
        }
        else
        {
            for (int i = 0; i < categories.size();i++)
            {
                selectCategory = ((Category) categories.get(i)).getByName(name);
                if (selectCategory != null)
                    break;
            }
        }

        return selectCategory;
    }

    /**
     * Request로 요청된 URL을 기준으로 현재 하위로 선택된 메뉴인지를 검사하여 선택처리하고 해당 Menu를 반환한다.
     * @return 선택된 메뉴, 선택된 것이 없다면, null을 반환한다.
     */
    public Category getByRequestURL(HttpServletRequest req)
    {
        String url = req.getRequestURI() +
                ((req.getQueryString() != null) ? "?" + req.getQueryString() : "");
        return this.getByURL(url);
    }
}

