.class public Test
.super java/lang/Object

.method public <init>()V
  aload_0
  invokenonvirtual java/lang/Object/<init>()V
  return
.end method

.method public static main([Ljava/lang/String;)V
  .limit stack 4
  .limit locals 4
  
  iconst_0
  istore_1
  Loop1:
  iload_1
  bipush 10
  if_icmpge less0
  iconst_1
  goto greater0
  less0:
  iconst_0
  greater0:
  istore_2
  iload_2
  iconst_1
  iand
  iconst_1
  if_icmpeq Body1
  goto EndLoop1
  Body1:
  iload_1
  iconst_1
  iadd
  istore_3
  iload_3
  istore_1
  goto Loop1
  EndLoop1:
  return
  return
.end method

