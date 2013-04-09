package com.dotmarketing.portlets.categories.business;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.portlets.ContentletBaseTest;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.structure.factories.FieldFactory;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.structure.model.Structure;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Jonathan Gamba
 * Date: 4/8/13
 */
public class CategoryAPITest extends ContentletBaseTest {

    /**
     * Testing {@link CategoryAPI#getParents(Categorizable, com.liferay.portal.model.User, boolean)}
     *
     * @throws Exception
     * @see CategoryAPI
     * @see Category
     */
    @Test
    public void getParents () throws Exception {

        CategoryAPI categoryAPI = APILocator.getCategoryAPI();
        PermissionAPI permissionAPI = APILocator.getPermissionAPI();

        List<Category> categories = new ArrayList<Category>();

        //***************************************************************
        //Creating new categories

        //HibernateUtil.startTransaction();
        HibernateUtil.startLocalTransactionIfNeeded();

        //Adding the parent category
        Category parentCategory = new Category();
        parentCategory.setCategoryName( "Movies" );
        parentCategory.setKey( "movies" );
        parentCategory.setCategoryVelocityVarName( "movies" );
        parentCategory.setSortOrder( (String) null );
        parentCategory.setKeywords( null );
        //Saving it
        categoryAPI.save( null, parentCategory, user, false );

        //Creating child categories
        //New Child category
        Category childCategory1 = new Category();
        childCategory1.setCategoryName( "Action" );
        childCategory1.setKey( "action" );
        childCategory1.setCategoryVelocityVarName( "action" );
        childCategory1.setSortOrder( (String) null );
        childCategory1.setKeywords( null );
        //Saving it
        categoryAPI.save( parentCategory, childCategory1, user, false );
        categories.add( childCategory1 );
        //New Child category
        Category childCategory2 = new Category();
        childCategory2.setCategoryName( "Drama" );
        childCategory2.setKey( "drama" );
        childCategory2.setCategoryVelocityVarName( "drama" );
        childCategory2.setSortOrder( (String) null );
        childCategory2.setKeywords( null );
        //Saving it
        categoryAPI.save( parentCategory, childCategory2, user, false );
        categories.add( childCategory2 );

        HibernateUtil.commitTransaction();

        //***************************************************************
        //Verify If we find the parent for the categories we just added categories
        List<Category> parents = categoryAPI.getParents( childCategory1, user, false );
        assertNotNull( parents );
        assertTrue( parents.size() > 0 );
        assertEquals( parents.get( 0 ), parentCategory );

        parents = categoryAPI.getParents( childCategory2, user, false );
        assertNotNull( parents );
        assertTrue( parents.size() > 0 );
        assertEquals( parents.get( 0 ), parentCategory );

        //HibernateUtil.commitTransaction();

        //***************************************************************
        //Set up a new structure with categories

        HibernateUtil.startTransaction();

        //Create the new structure
        Structure testStructure = createStructure( "JUnit Test Categories Structure_" + String.valueOf( new Date().getTime() ), "junit_test_categories_structure_" + String.valueOf( new Date().getTime() ) );
        //Add a Text field
        Field textField = new Field( "JUnit Test Text", Field.FieldType.TEXT, Field.DataType.TEXT, testStructure, false, true, true, 1, false, false, false );
        FieldFactory.saveField( textField );
        //Add a Category field
        Field categoryField = new Field( "JUnit Movies", Field.FieldType.CATEGORY, Field.DataType.TEXT, testStructure, true, true, true, 2, false, false, true );
        categoryField.setValues( parentCategory.getInode() );
        FieldFactory.saveField( categoryField );

        //***************************************************************
        //Set up a content for the categories structure
        Contentlet contentlet = new Contentlet();
        contentlet.setStructureInode( testStructure.getInode() );
        contentlet.setHost( defaultHost.getIdentifier() );
        contentlet.setLanguageId( APILocator.getLanguageAPI().getDefaultLanguage().getId() );

        //Validate if the contenlet is OK
        contentletAPI.validateContentlet( contentlet, categories );

        //Saving the contentlet
        contentlet = APILocator.getContentletAPI().checkin( contentlet, categories, permissionAPI.getPermissions( contentlet, false, true ), user, false );
        APILocator.getContentletAPI().isInodeIndexed( contentlet.getInode() );
        APILocator.getVersionableAPI().setLive( contentlet );

        HibernateUtil.commitTransaction();

        //***************************************************************
        //Verify If we find the parent for these categories
        parents = categoryAPI.getParents( contentlet, user, false );
        assertNotNull( parents );
        assertTrue( parents.size() > 0 );
    }

}