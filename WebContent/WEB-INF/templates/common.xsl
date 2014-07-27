<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:template name="linebreak">
		<xsl:text>&lt;br /&gt;</xsl:text>
	</xsl:template>
	<xsl:template name="htmLink">
		<xsl:param name="dest" select="UNDEFINED" /> <!--default value -->
		<xsl:element name="a">
			<xsl:attribute name="href">
        <xsl:value-of select="$dest"></xsl:value-of> <!--link target-->
      </xsl:attribute>
			<xsl:text>Link</xsl:text>
		</xsl:element>
	</xsl:template>
	<xsl:template name="htmLink2">
		<xsl:param name="dest" select="UNDEFINED" /> <!--default value -->
		<xsl:text>&lt;a class="boldText" href="</xsl:text>
		<xsl:value-of select="$dest"></xsl:value-of>
		<xsl:text>" target='_blank' &gt; </xsl:text>
		<xsl:text>Link&lt;/a&gt;</xsl:text>
	</xsl:template>
</xsl:stylesheet>


