.class public OverloadingSuper
.super java/lang/Object

.field a [I 
.method public <init>()V
  aload_0
  invokenonvirtual java/lang/Object/<init>()V
  return
.end method

.method public getElement(I)I
  .limit stack 2
  .limit locals 5
  
  aload_0
  getfield OverloadingSuper/a [I
  astore_2
  iload_1
  istore_3
  aload_2
  iload_3
  iaload
  istore 4
  iload 4
  ireturn
.end method

.method public initializeArray()I
  .limit stack 6
  .limit locals 12
  
  bipush 10
  newarray int
  
  astore_1
  aload_0
  aload_1
  
  putfield OverloadingSuper/a [I
  iconst_0
  istore_2
  Loop1:
  aload_0
  getfield OverloadingSuper/a [I
  astore_3
  aload_3
  arraylength
  istore 4
  iload_2
  iload 4
  if_icmpge less0
  iconst_1
  goto greater0
  less0:
  iconst_0
  greater0:
  istore 5
  iload 5
  iconst_1
  iand
  iconst_1
  if_icmpeq Body1
  goto EndLoop1
  Body1:
  iload_2
  iconst_2
  imul
  istore 6
  iload_2
  istore 7
  aload_0
  getfield OverloadingSuper/a [I
  astore 8
  aload 8
  iload 7
  iload 6
  iastore
  iload_2
  iconst_1
  iadd
  istore 9
  iload 9
  istore_2
  goto Loop1
  EndLoop1:
  aload_0
  getfield OverloadingSuper/a [I
  astore 10
  aload 10
  arraylength
  istore 11
  iload 11
  ireturn
.end method

.method public sum(IZ)I
  .limit stack 4
  .limit locals 5
  
  iload_2
  iconst_1
  iand
  iconst_1
  if_icmpeq then1
  iload_1
  istore_3
  goto endif1
  then1:
  iconst_0
  iload_1
  isub
  istore 4
  iload 4
  istore_3
  endif1:
  iload_3
  ireturn
.end method

.method public static main([Ljava/lang/String;)V
  .limit stack 4
  .limit locals 6
  
  new OverloadingSuper
  dup
  
  astore_1
  invokespecial OverloadingSuper.<init>()V
  aload_1
  astore_2
  aload_2
  invokevirtual OverloadingSuper.initializeArray()I
  istore_3
  aload_2
  iconst_4
  invokevirtual OverloadingSuper.getElement(I)I
  istore 4
  iload 4
  istore 5
  iload 5
  invokestatic io.print(I)V
  return
.end method

