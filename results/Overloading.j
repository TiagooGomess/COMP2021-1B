.class public Overloading
.super OverloadingSuper

.method public <init>()V
  aload_0
  invokenonvirtual OverloadingSuper/<init>()V
  return
.end method

.method public sum(II)I
  .limit stack 2
  .limit locals 4
  
  iload_1
  iload_2
  iadd
  istore_3
  iload_3
  ireturn
.end method

.method public sum(III)I
  .limit stack 2
  .limit locals 6
  
  iload_2
  iload_3
  iadd
  istore 4
  iload_1
  iload 4
  iadd
  istore 5
  iload 5
  ireturn
.end method

.method public sum(IIZ)I
  .limit stack 6
  .limit locals 8
  
  iload_3
  iconst_1
  iand
  iconst_1
  if_icmpeq then1
  aload_0
  iload_1
  iload_2
  invokevirtual Overloading.sum(II)I
  istore 4
  iload 4
  istore 5
  goto endif1
  then1:
  aload_0
  iload_1
  iload_2
  invokevirtual Overloading.sum(II)I
  istore 6
  iconst_0
  iload 6
  isub
  istore 7
  iload 7
  istore 5
  endif1:
  iload 5
  ireturn
.end method

.method public static main([Ljava/lang/String;)V
  .limit stack 11
  .limit locals 7
  
  new Overloading
  dup
  
  astore_1
  invokespecial Overloading.<init>()V
  aload_1
  astore_2
  aload_2
  iconst_1
  iconst_2
  invokevirtual Overloading.sum(II)I
  istore_3
  iload_3
  invokestatic io.println(I)V
  aload_2
  iconst_1
  iconst_1
  invokevirtual Overloading.sum(IZ)I
  istore 4
  iload 4
  invokestatic io.println(I)V
  aload_2
  iconst_1
  iconst_2
  iconst_3
  invokevirtual Overloading.sum(III)I
  istore 5
  iload 5
  invokestatic io.println(I)V
  aload_2
  iconst_1
  iconst_2
  iconst_1
  invokevirtual Overloading.sum(IIZ)I
  istore 6
  iload 6
  invokestatic io.println(I)V
  return
.end method

