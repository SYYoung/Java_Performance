/**
 * 
 */
package spelling;

//import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * WPTree implements WordPath by dynamically creating a tree of words during a Breadth First
 * Search of Nearby words to create a path between two words. 
 * 
 * @author UC San Diego Intermediate MOOC team
 *
 */
public class WPTree implements WordPath {

	// this is the root node of the WPTree
	private WPTreeNode root;
	// used to search for nearby Words
	private NearbyWords nw; 
	
	private static final int THRESHOLD = 6000; 
	
	// This constructor is used by the Text Editor Application
	// You'll need to create your own NearbyWords object here.
	public WPTree () {
		this.root = null;
		// TODO initialize a NearbyWords object
		 Dictionary d = new DictionaryHashSet();
		 DictionaryLoader.loadDictionary(d, "data/dict.txt");
		 this.nw = new NearbyWords(d);
	}
	
	//This constructor will be used by the grader code
	public WPTree (NearbyWords nw) {
		this.root = null;
		this.nw = nw;
	}
	
	// see method description in WordPath interface
	public List<String> findPath(String word1, String word2) 
	{
	    // TODO: Implement this method.
		/*
		 Create a queue of WPTreeNodes to hold words to explore
		Create a visited set to avoid looking at the same word repeatedly

		Set the root to be a WPTreeNode containing word1
		Add the initial word to visited
		Add root to the queue 

		while the queue has elements and we have not yet found word2
  			remove the node from the start of the queue and assign to curr
  			get a list of real word neighbors (one mutation from curr's word)
  			for each n in the list of neighbors
     			if n is not visited
       				add n as a child of curr 
       				add n to the visited set
       				add the node for n to the back of the queue
       				if n is word2
          				return the path from child to root	
		 */
		// we must verify whether both word1 and word2 are valid words. if not, return null 
		if (!this.nw.isRealWord(word1) || !this.nw.isRealWord(word2)) {
			System.out.println("Either " + word1 + " or " + word2 + " is not a valid word. ");
			return null;
		}
		List<WPTreeNode> queue = new LinkedList<WPTreeNode>();
		List<String> visited = new LinkedList<String>();
		WPTreeNode newNode = new WPTreeNode(word1, null);
		this.root = newNode;
		visited.add(newNode.getWord());
		queue.add(newNode);
		WPTreeNode curr = root;
		
		boolean found = false;
		while (!queue.isEmpty() && !found) {
			// if we have already visited more than threshold words, we just quit
			if (visited.size() > THRESHOLD) {
				break;
			}
			System.out.println("Number of words have been visited: " + visited.size());
			curr = queue.remove(0);
			List<String> potential = this.nw.distanceOne(curr.getWord(), true);
			for (String s : potential) {
				if (!visited.contains(s)) {
					WPTreeNode node = curr.addChild(s);
					visited.add(0, s);
					queue.add(node);
					if (s.equals(word2)) {
						found = true;
						curr = node;
						break;
					}
				}
			}
		}
		if (found) {
			// return the path from child to root
			List<String> path = curr.buildPathToRoot();
			System.out.println("The path from " + word1 + " to " + word2 + " is : " + path);
			return path;
		}
		else {
			return null;
		}
	}
	
	// Method to print a list of WPTreeNodes (useful for debugging)
	private String printQueue(List<WPTreeNode> list) {
		String ret = "[ ";
		
		for (WPTreeNode w : list) {
			ret+= w.getWord()+", ";
		}
		ret+= "]";
		return ret;
	}
	
	public static void main(String[] args) {
		WPTree wordTree = new WPTree();
		String word9 = "pool", word10 = "spoon";
		String word3 = "stools", word4 = "moon";
		String word5 = "needle", word6 = "kitten";
		String word7 = "time",  word8 = "theme";
		String word11 = "foal", word12 = "needles";
		String word13 = "aeb", word14 = "air";
		
		String word1 = word13;
		String word2 = word14;
		
		List<String> thePath = wordTree.findPath(word1, word2);
		System.out.println("result in main from : " + word1 + " to " + word2 + " is : ");
		if (thePath != null) {
			System.out.println(thePath);
		}
		else {
			System.out.println("After " + THRESHOLD + " of trials, fail to find such path.");
		}
	}
	
}

/* Tree Node in a WordPath Tree. This is a standard tree with each
 * node having any number of possible children.  Each node should only
 * contain a word in the dictionary and the relationship between nodes is
 * that a child is one character mutation (deletion, insertion, or
 * substitution) away from its parent
*/
class WPTreeNode {
    
    private String word;
    private List<WPTreeNode> children;
    private WPTreeNode parent;
    
    /** Construct a node with the word w and the parent p
     *  (pass a null parent to construct the root)  
	 * @param w The new node's word
	 * @param p The new node's parent
	 */
    public WPTreeNode(String w, WPTreeNode p) {
        this.word = w;
        this.parent = p;
        this.children = new LinkedList<WPTreeNode>();
    }
    
    /** Add a child of a node containing the String s
     *  precondition: The word is not already a child of this node
     * @param s The child node's word
	 * @return The new WPTreeNode
	 */
    public WPTreeNode addChild(String s){
        WPTreeNode child = new WPTreeNode(s, this);
        this.children.add(child);
        return child;
    }
    
    /** Get the list of children of the calling object
     *  (pass a null parent to construct the root)  
	 * @return List of WPTreeNode children
	 */
    public List<WPTreeNode> getChildren() {
        return this.children;
    }
   
    /** Allows you to build a path from the root node to 
     *  the calling object
     * @return The list of strings starting at the root and 
     *         ending at the calling object
	 */
    public List<String> buildPathToRoot() {
        WPTreeNode curr = this;
        List<String> path = new LinkedList<String>();
        while(curr != null) {
            path.add(0,curr.getWord());
            curr = curr.parent; 
        }
        return path;
    }
    
    /** Get the word for the calling object
     *
	 * @return Getter for calling object's word
	 */
    public String getWord() {
        return this.word;
    }
    
    /** toString method
    *
	 * @return The string representation of a WPTreeNode
	 */
    public String toString() {
        String ret = "Word: "+word+", parent = ";
        if(this.parent == null) {
           ret+="null.\n";
        }
        else {
           ret += this.parent.getWord()+"\n";
        }
        ret+="[ ";
        for(WPTreeNode curr: children) {
            ret+=curr.getWord() + ", ";
        }
        ret+=(" ]\n");
        return ret;
    }

}

