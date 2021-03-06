/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.subclass;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.SpellProhibitor;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.ProhibitedSpellType;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class ChoiceTokenTest extends AbstractTokenTestCase<SubClass>
{

	static ChoiceToken token = new ChoiceToken();
	static CDOMTokenLoader<SubClass> loader = new CDOMTokenLoader<SubClass>();

	@Override
	public Class<SubClass> getCDOMClass()
	{
		return SubClass.class;
	}

	@Override
	public CDOMLoader<SubClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<SubClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyType() throws PersistenceLayerException
	{
		assertFalse(parse("SCHOOL"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoValue() throws PersistenceLayerException
	{
		assertFalse(parse("SCHOOL|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoType() throws PersistenceLayerException
	{
		assertFalse(parse("|Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("|SCHOOL|Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("SCHOOL|Good|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublPipe() throws PersistenceLayerException
	{
		assertFalse(parse("SCHOOL||Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTripleContent() throws PersistenceLayerException
	{
		assertFalse(parse("SCHOOL|Bad|Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotAType() throws PersistenceLayerException
	{
		assertFalse(parse("NOTATYPE|Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputIllegalType() throws PersistenceLayerException
	{
		assertFalse(parse("ALIGNMENT|LawfulGood"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinDescriptorSimple()
			throws PersistenceLayerException
	{
		runRoundRobin("DESCRIPTOR|Fire");
	}

	@Test
	public void testRoundRobinSchoolSimple() throws PersistenceLayerException
	{
		runRoundRobin("SCHOOL|Evocation");
	}

	@Test
	public void testRoundRobinSubSchoolSimple()
			throws PersistenceLayerException
	{
		runRoundRobin("SUBSCHOOL|Subsch");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "SUBSCHOOL|Subsch";
	}

	@Override
	protected String getLegalValue()
	{
		return "SCHOOL|Evocation";
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	private ObjectKey<SpellProhibitor> getObjectKey()
	{
		return ObjectKey.CHOICE;
	}

	@Test
	public void testUnparseLegalSchool() throws PersistenceLayerException
	{
		SpellProhibitor o = getConstant(ProhibitedSpellType.SCHOOL, "Public");
		primaryProf.put(getObjectKey(), o);
		expectSingle(getToken().unparse(primaryContext, primaryProf), "SCHOOL|Public");
	}

	@Test
	public void testUnparseLegalSubSchool() throws PersistenceLayerException
	{
		SpellProhibitor o = getConstant(ProhibitedSpellType.SUBSCHOOL, "Elementary");
		primaryProf.put(getObjectKey(), o);
		expectSingle(getToken().unparse(primaryContext, primaryProf), "SUBSCHOOL|Elementary");
	}

	@Test
	public void testUnparseLegalDescriptor() throws PersistenceLayerException
	{
		SpellProhibitor o = getConstant(ProhibitedSpellType.DESCRIPTOR, "Fire");
		primaryProf.put(getObjectKey(), o);
		expectSingle(getToken().unparse(primaryContext, primaryProf), "DESCRIPTOR|Fire");
	}

	private SpellProhibitor getConstant(ProhibitedSpellType type, String args)
	{
		SpellProhibitor spellProb = new SpellProhibitor();
		spellProb.setType(type);
		spellProb.addValue(args);
		return spellProb;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFail() throws PersistenceLayerException
	{
		ObjectKey objectKey = getObjectKey();
		primaryProf.put(objectKey, new Object());
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (ClassCastException e)
		{
			//Yep!
		}
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
