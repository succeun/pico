package pico.console;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import pico.LinkUtil;

/**
 * Ʈ�������� �޴��� ����� ����, ������ �޴��� ��Ÿ���� Ŭ�����̴�.
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
    private boolean select; // ���ÿ���
    private boolean isVisablity = true;
    private boolean isPopup; // ���ο� â���� ��ũ�� ����� ����

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
     * �޴��� �����.
     * @param parent �θ� �޴�
     * @param name ȭ����� �̸�
     */
    public Category(HttpServletRequest req, Category parent, int id, String name)
    {
        this(req, parent, id, name, null, null, null, null);
    }

    /**
     * �޴��� �����.
     * URL�� ��Ʈ�Ѱ� �޼ҵ������ ������.
     * @param parent �θ� �޴�
     * @param name ȭ����� �̸�
     * @param controller ��Ʈ�ѷ� Class
     * @param method �޼ҵ��
     */
    public Category(HttpServletRequest req, Category parent, int id, String name, Class<?> controller, String method)
    {
        this(req, parent, id, name, controller, method, null, null);
    }

    /**
     * �޴��� �����.
     * URL�� ��Ʈ�Ѱ� �޼ҵ������ ������.
     * @param parent �θ� �޴�
     * @param name ȭ����� �̸�
     * @param controller ��Ʈ�ѷ� Class
     * @param method �޼ҵ��
     * @param urlParam Ư�� URL Parameter
     */
    public Category(HttpServletRequest req, Category parent, int id, String name, Class<?> controller, String method, String urlParam)
    {
        this(req, parent, id, name, controller, method, null, urlParam);
    }

    /**
     * �޴��� �����.
     * @param parent �θ� �޴�
     * @param name ȭ����� �̸�
     * @param url Ư�� URL
     */
    public Category(HttpServletRequest req, Category parent, int id, String name, String url)
    {
        this(req, parent, id, name, null, null, url, null);
    }

    /**
     * �޴��� �����.
     * @param parent �θ� �޴�
     * @param name ȭ����� �̸�
     * @param url Ư�� URL
     * @param urlParam Ư�� URL Parameter
     */
    public Category(HttpServletRequest req, Category parent, int id, String name, String url, String urlParam)
    {
        this(req, parent, id, name, null, null, url, urlParam);
    }

    /**
     * �޴��� �����.
     * @param parent �θ� �޴�
     * @param name ȭ����� �̸�
     */
    public Category(HttpServletRequest req, Category parent, String name)
    {
        this(req, parent, UNKNOWN_ID, name, null, null, null, null);
    }

    /**
     * �޴��� �����.
     * URL�� ��Ʈ�Ѱ� �޼ҵ������ ������.
     * @param parent �θ� �޴�
     * @param name ȭ����� �̸�
     * @param controller ��Ʈ�ѷ� Class
     * @param method �޼ҵ��
     */
    public Category(HttpServletRequest req, Category parent, String name, Class<?> controller, String method)
    {
        this(req, parent, UNKNOWN_ID, name, controller, method, null, null);
    }

    /**
     * �޴��� �����.
     * URL�� ��Ʈ�Ѱ� �޼ҵ������ ������.
     * @param parent �θ� �޴�
     * @param name ȭ����� �̸�
     * @param controller ��Ʈ�ѷ� Class
     * @param method �޼ҵ��
     * @param urlParam Ư�� URL Parameter
     */
    public Category(HttpServletRequest req, Category parent, String name, Class<?> controller, String method, String urlParam)
    {
        this(req, parent, UNKNOWN_ID, name, controller, method, null, urlParam);
    }

    /**
     * �޴��� �����.
     * @param parent �θ� �޴�
     * @param name ȭ����� �̸�
     * @param url Ư�� URL
     */
    public Category(HttpServletRequest req, Category parent, String name, String url)
    {
        this(req, parent, UNKNOWN_ID, name, null, null, url, null);
    }

    /**
     * �޴��� �����.
     * @param parent �θ� �޴�
     * @param name ȭ����� �̸�
     * @param url Ư�� URL
     * @param urlParam Ư�� URL Parameter
     */
    public Category(HttpServletRequest req, Category parent, String name, String url, String urlParam)
    {
        this(req, parent, UNKNOWN_ID, name, null, null, url, urlParam);
    }

    /**
     * �޴��� �����.
     * @param parent �θ� �޴�
     * @param id id
     * @param name ȭ����� �̸�
     * @param controller ��Ʈ�ѷ� Class
     * @param method �޼ҵ��
     * @param url Ư�� URL
     * @param urlParam Ư�� URL Parameter
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
     * �ڽ� �޴����� ��ȯ�Ѵ�.
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
     * �������� ���θ� �����Ѵ�.
     * @return �������� ����
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
     * ���õ� �޴����� ���θ� ��ȯ�Ѵ�.
     * @return ���ÿ���
     */
    public boolean isSelect()
    {
        return this.select;
    }

    /**
     * �޴��� ����ó���Ͽ�, �θ�޴����α��� ó���Ѵ�.
     */
    public void setSelect()
    {
        this.select = true;
        if (parent != null)
            this.parent.setSelect();
    }

    /**
     * ���� �޴��� ��Ʈ�κ����� �н��� ��´�.
     * @return �н�
     */
    public String getPath()
    {
        return getPath(false);
    }

    /**
     * ���� �޴��� ��Ʈ�κ����� �н��� ��´�.
     * @param isContainLink link�� �������� ����
     * @return �н�
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
     * ���õ� �޴��� �н��� ��Ʈ���� �˻��Ͽ� ��ȯ�Ѵ�.
     * ��, ������ �̷�� ���� �Ѵ�.
     * @return ���õ� �޴��� �н�
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
     * �޴��� ���� �ֻ��� Root �޴��� ��ȯ�Ѵ�.
     * @return  ��Ʈ �޴�
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
     * ���� ������ ���õ� �޴������� �˻��Ͽ� ����ó���ϰ� �ش� Menu�� ��ȯ�Ѵ�.
     * @param classFullName Ŭ������
     * @param method ��
     * @return ���õ� �޴�, ���õ� ���� ���ٸ�, null�� ��ȯ�Ѵ�.
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
     * ���� ������ ���õ� �޴������� �˻��Ͽ� ����ó���ϰ� �ش� Menu�� ��ȯ�Ѵ�.
     * @param url URL, �޴��� ������ URL�� �˻��Ͽ� ����ó���Ѵ�.
     * @return ���õ� �޴�, ���õ� ���� ���ٸ�, null�� ��ȯ�Ѵ�.
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
     * ���� ������ ���õ� �޴������� �˻��Ͽ� ����ó���ϰ� �ش� Menu�� ��ȯ�Ѵ�.
     * @param id ���̵�
     * @return ���õ� �޴�, ���õ� ���� ���ٸ�, null�� ��ȯ�Ѵ�.
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
     * ���� ������ ���õ� �޴������� �˻��Ͽ� ����ó���ϰ� �ش� Menu�� ��ȯ�Ѵ�.
     * @param name �̸�
     * @return ���õ� �޴�, ���õ� ���� ���ٸ�, null�� ��ȯ�Ѵ�.
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
     * Request�� ��û�� URL�� �������� ���� ������ ���õ� �޴������� �˻��Ͽ� ����ó���ϰ� �ش� Menu�� ��ȯ�Ѵ�.
     * @return ���õ� �޴�, ���õ� ���� ���ٸ�, null�� ��ȯ�Ѵ�.
     */
    public Category getByRequestURL(HttpServletRequest req)
    {
        String url = req.getRequestURI() +
                ((req.getQueryString() != null) ? "?" + req.getQueryString() : "");
        return this.getByURL(url);
    }
}

