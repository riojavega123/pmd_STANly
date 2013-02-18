package net.sourceforge.pmd.lang.java.rule.stanly;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.rule.stanly.aftercalculator.AbstractAfterCalculator;
import net.sourceforge.pmd.lang.java.rule.stanly.aftercalculator.Fat;
import net.sourceforge.pmd.lang.java.rule.stanly.element.ElementNode;
import net.sourceforge.pmd.lang.java.rule.stanly.element.ElementNodeType;
import net.sourceforge.pmd.lang.java.rule.stanly.element.LibraryDomain;
import net.sourceforge.pmd.lang.java.rule.stanly.element.ProjectDomain;

public class AfterRelations {
	private static ProjectDomain projectNode = null;
	private RelationManager manager = null;
	private List<AbstractAfterCalculator> calculators = null;

	public AfterRelations(ProjectDomain projectNode, RelationManager manager) {
		// TODO Auto-generated constructor stub
		this.projectNode = projectNode;
		this.manager = manager;

		if(calculators == null)
		{
			calculators = new ArrayList<AbstractAfterCalculator>();
			calculators.add(new Fat());
		}
	}
	public void analysis(){
		// TODO Auto-generated method stub
		FindTarget();
		RemoveNullTargetRelations();
		for(AbstractAfterCalculator calculator:calculators)
			calculator.calcMatric(projectNode);
	}

	public void FindTarget(){
		List<DomainRelation> domainRelation = manager.getDomainRelationList();
		String targetString;
		ElementNode sourceNode;
		ElementNode targetNode;

		for(DomainRelation relation:domainRelation)
		{
			//sourceString = domainRelation.get(i).getSource();
			//if(relation.getSource().equals("org.simpleframework.xml.convert.AnnotationStrategy.write"))
			//	System.out.println("");
			
			targetString = relation.getTarget();
			sourceNode = relation.getSourceNode();
			targetNode = sourceNode.getParent().findNode(targetString);

			
			
			if(targetNode == null)//찾을수 없느 관계는 추후 삭제함 YHC
				;//domainRelation.remove(relation);
			else
			{
				relation.setTargetNode(targetNode);
				//System.out.println(targetNode.getFullName() + " == " + targetString);
				System.out.println(sourceNode.getFullName() + " --" + relation.getRelation().name() + "--> " + targetNode.getFullName());
			}
		}		
	}
	public void RemoveNullTargetRelations(){//찾을수 없느 관계는 여기서 삭제함 YHC
		List<DomainRelation> domainRelation = manager.getDomainRelationList();
		ElementNode sourceNode;
		ElementNode targetNode;
		for(int i=0;i<domainRelation.size();i++)
		{
			if(domainRelation.get(i).getTargetNode() == null)	domainRelation.remove(i--);
			else
			{
				DomainRelation relation = domainRelation.get(i);
				sourceNode = relation.getSourceNode();
				targetNode = relation.getTargetNode();
				sourceNode.AddRelationTarget(relation);
				targetNode.AddRelationSource(relation);
			}
		}
	}
	public void makePackageSet() {
		// TODO Auto-generated method stub
		for(ElementNode libd:projectNode.getChildren())
			makePackageSet(libd);
		//makePackageSet(projectNode);
	}
	public void makePackageSet(ElementNode node)
	{

		if(node.getChildren().size() == 0)	return;
		boolean newFlag = false;
		ElementNode newNode = null;
		List<String> packageSetNames = new ArrayList<String>();
		String[] minStr = node.getChildren().get(0).getFullName().split("\\."); 
		String[] newStr;
		for(ElementNode child:node.getChildren())
		{
			String[] cmpStr = child.getFullName().split("\\.");
			newStr = getMinimumMatch(minStr,cmpStr);
			if(newStr.length > 0)
			{
				newFlag = true;
				minStr = newStr;
			}
			else 
			{
				if(newFlag)
					packageSetNames.add(mergeStrArray(minStr));
				newFlag = false;
				minStr = cmpStr;
			}
		}
		if(newFlag)
			packageSetNames.add(mergeStrArray(minStr));
		for(String packageSetName:packageSetNames)
		{
			newNode = node.addChildren(ElementNodeType.PACKAGESET, packageSetName);
			for(int i=0;i<node.getChildren().size();i++)
			{
				ElementNode child = node.getChildren().get(i); 
				if(newNode == child)	continue;
				if(	child.getName().startsWith(packageSetName) )
				{
					if(child.getName().equals(packageSetName))
						child.setName(".");
					else
						child.setName(child.getName().substring(packageSetName.length()+1));
					newNode.addChildren(child);
					child.setParent(newNode);
					node.getChildren().remove(i--);
				}
			}
			makePackageSet(newNode);
		}
	}
	private String mergeStrArray(String[] strs)
	{
		String mergeStr = strs[0];
		for(int i=1;i<strs.length;i++)
			mergeStr += "." + strs[i];
		return (mergeStr);
	}
	private String[] getMinimumMatch(String[] strA, String[] strB)
	{
		List<String> strC = new ArrayList<String>();
		int length = strA.length < strB.length ? strA.length : strB.length;
		for(int i=0;i<length;i++)
		{
			if(strA[i].equals(strB[i]))
				strC.add(strA[i]);
		}
		return (String[])strC.toArray(new String[0]);
	}
}
