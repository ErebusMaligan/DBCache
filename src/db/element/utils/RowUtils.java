package db.element.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import db.element.Row;
import db.element.Table;
import db.element.column.ColumnData;
import db.exception.MissingPKsException;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Dec 31, 2013, 6:31:49 AM 
 */
public class RowUtils {
	
	public static boolean valEqual( Row primary, Row other ) {
		boolean ret = true;
		if ( primary.getValues().size() == other.getValues().size() ) {
			for ( String s : primary.getValues().keySet() ) {
				ColumnData val = primary.getColumn( s );
				ColumnData oVal = other.getColumn( s );
				if ( oVal != null ) {
					if ( ( val.getValue() != null && oVal.getValue() != null ) || ( val.getValue() == null && oVal.getValue() == null ) ) {
						if ( !val.getValue().equals( oVal.getValue() ) ) {
							ret = false;
							break;
						}
					}
				}
			}	
		} else {
			ret = false;
		}
		return ret;
	}
	
	//Adds empty column data of the appropriate type if a row has been submitted with partial fill
	public static Row completeRow( Row in, Table definition ) {
		Row ret = in;
		for ( ColumnData c : definition.getDefinition().getValues().values() ) {
			if ( in.getColumn( c.getName() ) == null ) {
				in.setColumn( c.getName(), c.clone() );
				in.getColumn( c.getName() ).setNull();
			}
		}
		return ret;
	}
	
	public static boolean pkEqual( Row primary, Row other, Table definition ) {  //TODO: keep an eye on this
		boolean ret = true;
//		if ( primary.getValues().size() == other.getValues().size() ) {
			for ( String s : primary.getValues().keySet() ) {
				if ( definition.getPKs().contains( s ) ) {
					ColumnData val = primary.getColumn( s );
					ColumnData oVal = other.getColumn( s ); 
//					System.out.println( "Val: " + val );
//					System.out.println( "oVAL: " + oVal );
//					if ( oVal != null ) {
						if /*(*/ ( val.getValue() != null && oVal.getValue() != null ) { // || ( val.getValue() == null && oVal.getValue() == null ) ) {
							if ( !val.getValue().equals( oVal.getValue() ) ) {
								ret = false;
//								System.out.println( "FALSE LEGIT" );
								break;
							}
						} else {   //THIS SECTION WAS ADDED LATER
							ret = false;
						}
//					}
				}
			}	
//		} else {
//			ret = false;
//			System.out.println( "FALSE UNEVEN" );
//		}
		return ret;
	}
	
	public static String getInserString( Row r ) {
		String ret = new String( " (" );
		boolean once = false;
		for ( ColumnData c : r.getValues().values() ) {
			if ( once ) {
				ret += ", ";
			}
			ret += c.getName();
			once = true;
		}
		ret += ") VALUES (";
		for ( int i = 0; i < r.getValues().size(); i++ ) {
			ret += i != 0 ? ", ?" : "?";
		}
		ret += ")";
		return ret;
	}
	
	public static List<ColumnData> getInsertValues( Row r ) {
		return new ArrayList<ColumnData>( r.getValues().values() );
	}
	
	public static String getUpdateString( Row r, Table definition ) throws MissingPKsException {
		if ( definition.getPKs().size() == 0 ) {
			throw new MissingPKsException( "Table definition " + definition.getName() + " contains no PKs" );
		}
		String ret = new String();
		boolean once = false;
		for ( ColumnData c : definition.getDefinition().getValues().values() ) {
			if ( !definition.getPKs().contains( c.getName() ) ) {
				if ( once ) {
					ret += ", ";
				}
				ret += c.getName() + " = ?";
				once = true;
			}
		}
		once = false;
		ret += " WHERE ";
		for ( String s : definition.getPKs() ) {
			if ( once ) {
				ret += " AND ";
			}
			if ( r.getValues().containsKey( s ) ) {
				ret += s + " = ?";
				once = true;
			} else {
				throw new MissingPKsException( "Expected PK " + s + " is not defined for the given row" );
			}
		}
		return ret;
	}
	
	public static List<ColumnData> getUpdateValues( Row r, Table definition ) {
		Vector<ColumnData> ret = new Vector<ColumnData>();
		for ( ColumnData c : definition.getDefinition().getValues().values() ) {
			if ( !definition.getPKs().contains( c.getName() ) ) {
				ret.add( r.getColumn( c.getName() ) );
			}
		}
		for ( String s : definition.getPKs() ) {
			ret.add( r.getColumn( s ) );
		}
		return ret;
	}
	
	public static String getDeleteString( Row r, Table definition ) throws MissingPKsException {
		if ( definition.getPKs().size() == 0 ) {
			throw new MissingPKsException( "Table definition " + definition.getName() + " contains no PKs" );
		}
		String ret = new String();
		boolean once = false;
		for ( String s : definition.getPKs() ) {
			if ( once ) {
				ret += " AND ";
			}
//			System.out.println( r );
//			System.out.println( "KEY: " + s );
//			for ( String q : r.getValues().keySet() ) {
//				System.out.println( q );
//			}
			if ( r.getValues().containsKey( s ) ) {
				ret += s + " = ?";
				once = true;
			} else {
				throw new MissingPKsException( "Expected PK " + s + " is not defined for the given row" );
			}
		}
		return ret;
	}
	
	public static List<ColumnData> getDeleteValues( Row r, Table definition ) {
		Vector<ColumnData> ret = new Vector<ColumnData>();
		for ( String s : definition.getPKs() ) {
			ret.add( r.getColumn( s ) );
		}
		return ret;
	}
	
	public static Row getRowFromRS( ResultSet rs, Table table ) throws SQLException {
//		System.out.println( table );
		List<ColumnData> columns = new Vector<ColumnData>();
		for ( ColumnData col : table.getDefinition().getValues().values() ) {
			ColumnData n = col.clone();
			n.fromRS( rs );
			columns.add( n );
		}
		return new Row( table.getName(), columns );
	}
	
	public static byte[] serialize( Row r ) {
		byte[] ret = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream( bos );
			out.writeObject( r );
			ret = bos.toByteArray();
		} catch ( IOException e ) {
			e.printStackTrace();
		} finally {
			if ( out != null ) {
				try {
					out.close();
				} catch ( IOException e ) {}
			}
			try {
				bos.close();
			} catch ( IOException e ) {}
		}
		return ret;
	}
	
	public static Row deserialize( byte[] r ) {
		Row ret = null;
		ByteArrayInputStream bis = new ByteArrayInputStream( r );
		ObjectInput in = null;
		try {
			in = new ObjectInputStream( bis );
			ret = (Row)in.readObject();
		} catch ( IOException | ClassNotFoundException e ) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch ( IOException e ) {
			}
			try {
				if ( in != null ) {
					in.close();
				}
			} catch ( IOException e ) {
			}
		}
		return ret;
	}

}