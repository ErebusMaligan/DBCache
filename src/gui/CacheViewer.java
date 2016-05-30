package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import cache.DataSourceProxy;
import cache.handler.CacheListener;
import db.element.Row;
import db.element.Table;
import db.element.utils.RowUtils;

/**
 * @author Daniel J. Rivers
 *         2014
 *
 * Created: Jan 19, 2014, 6:22:13 AM
 */
public class CacheViewer extends JPanel {

    private static final long serialVersionUID = 1L;
   
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Cache" );
   
    private DefaultTreeModel model = new DefaultTreeModel( root );
   
    private JTree tree = new JTree( model );
   
    private CacheListener l;
   
    private Map<String, DefaultMutableTreeNode> nodes = new HashMap<String, DefaultMutableTreeNode>();
   
    public CacheViewer() {
        tree.setCellRenderer( new CacheTreeCellRenderer() );
        this.setLayout( new BorderLayout() );
        this.add( new JScrollPane( tree ), BorderLayout.CENTER );
        JButton print = new JButton( "Print Cache" );
        print.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                DataSourceProxy.getInstance().getCache().printCache();
            }
        } );
        JPanel p = new JPanel();
        p.add( print );
        JButton printNm = new JButton( "Print Stats Only" );
        printNm.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for ( Table t : DataSourceProxy.getInstance().getCache().getTables() ) {
                    System.out.println( t.getName() + " Rows: " + DataSourceProxy.getInstance().getCache().getAllRows( t.getName() ).size() );
                }
            }
        } );
        p.add( Box.createRigidArea( new Dimension( 10, 10 ) ) );
        p.add( printNm );
        this.add( p, BorderLayout.SOUTH );
        rebuildTree();
        init();
    }
   
    public synchronized void definitionAdded( Table t ) {
        if ( nodes.get( t.getName() ) == null ) {
            DataSourceProxy.getInstance().getCache().getHandler( t.getName() ).addListener( l );
            addTable( t );
        }
    }
   
    private void init() {
        l = new CacheListener() {
            @Override
            public void created( Row r ) {
                addRow( r );
            }

            @Override
            public void deleted( Row r ) {
                deleteRow( r );
            }

            @Override
            public void updated( Row r ) {
                updateRow( r );
            }
        };
        for ( Table t : DataSourceProxy.getInstance().getCache().getTables() ) {
            DataSourceProxy.getInstance().getCache().getHandler( t.getName() ).addListener( l );
        }
    }
   
    private synchronized void rebuildTree() {
        root.removeAllChildren();
        for ( Table t : DataSourceProxy.getInstance().getCache().getTables() ) {
            addTable( t );
            for ( Row r : DataSourceProxy.getInstance().getCache().getAllRows( t.getName() ) ) {
                addRow( r );
            }
        }
        model.reload();
    }
   
    private DefaultMutableTreeNode addTable( Table t ) {
        DefaultMutableTreeNode table = new DefaultMutableTreeNode( t );
        nodes.put( t.getName(), table );
        model.insertNodeInto( table, root, root.getChildCount() );
        return table;
    }
   
    private DefaultMutableTreeNode findExistingRow( Row r ) {
        DefaultMutableTreeNode table = nodes.get( r.getName() );
        DefaultMutableTreeNode ret = null;
        for ( int i = 0; i < table.getChildCount(); i++ ) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)table.getChildAt( i );
            if ( RowUtils.pkEqual( (Row)n.getUserObject(), r, (Table)table.getUserObject() ) ) {
                ret = n;
                break;
            }
        }
        return ret;
    }
   
    private void addRow( Row r ) {
        DefaultMutableTreeNode table = nodes.get( r.getName() );
        DefaultMutableTreeNode row = new DefaultMutableTreeNode( r );
        model.insertNodeInto( row, table, table.getChildCount() );
    }
   
    private void updateRow( Row r ) {
        DefaultMutableTreeNode e = findExistingRow( r );
        if ( e != null ) {
            model.removeNodeFromParent( e );
            addRow( r );
        }
    }
   
    private void deleteRow( Row r ) {
        DefaultMutableTreeNode e = findExistingRow( r );
        if ( e != null ) {
            model.removeNodeFromParent( e );
        }
    }
   
    private static class CacheTreeCellRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = 1L;
       
        @Override
         public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus ) {
            JLabel c = (JLabel)super.getTreeCellRendererComponent( tree, value, selected, expanded, leaf, row, hasFocus );
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            Object o = node.getUserObject();
            if ( o instanceof Row ) {
                c.setText( ((Row)o).toString().replaceAll( "\\n", ", " ).replaceAll( ", ,", "," ) );
                c.setForeground( Color.RED );
            } else if ( o instanceof Table ) {
                Table t = (Table)o;
                String tS = t.getName() + "  ";
                for ( String s : t.getPKs() ) {
                    tS += "(" + s + ")  ";
                }
                c.setText( tS );
                c.setForeground( Color.GREEN );
            }
            return c;
         }
    }
}
